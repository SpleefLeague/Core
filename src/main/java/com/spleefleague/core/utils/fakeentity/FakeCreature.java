package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerAnimation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.spleefleague.core.utils.UtilAlgo;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public abstract class FakeCreature {
    
    protected final static double maxDistance = 64d;
    
    private final static Set<FakeCreature> alive = new HashSet<>();
    
    public static Set<FakeCreature> getAlive() {
        return alive;
    }
    
    protected static void removeAliveCreature(FakeCreature creature) {
        alive.remove(creature);
    }
    
    protected static void addAliveCreature(FakeCreature creature) {
        alive.add(creature);
    }
    
    protected static int getRandomId() {
        return UtilAlgo.r(Integer.MAX_VALUE - 400) + 300;
    }
    
    final int id;
    
    final World world;
    
    final Location location;
    
    boolean valid = false;
    
    final Collection<Player> affectedPlayers = new HashSet<>();
    
    boolean invalidated = false;
    
    protected FakeCreature(int id, Location location) {
        this.id = id;
        this.world = location.getWorld();
        this.location = location;
        FakeEntitiesManager.addCreature(id, this);
    }
    
    protected FakeCreature(Location location) {
        this(getRandomId(), location);
    }
    
    public int getId() {
        return id;
    }
    
    public World getWorld() {
        return world;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public Collection<Player> getAffectedPlayers() {
        return affectedPlayers;
    }
    
    protected abstract AbstractPacket[] getInfo();
    
    protected void onLeftClick(Player p) {}
    
    protected void onRightClick(Player p) {}
    
    public void onShowing(Player p) {}
    
    public void onHiding(Player p) {};
    
    protected void show(Player p) {
        for(AbstractPacket packet : getInfo())
            packet.sendPacket(p);
        updateRotation(p);
        onShowing(p);
    }
    
    protected void hide(Player p) {
        if(p.isOnline()) {
            WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
            destroy.setEntityIds(new int[]{getId()});
            destroy.sendPacket(p);
        }
        onHiding(p);
    }
    
    protected void tick() {
        Location loc = getLocation();
        Set<Player> toBeRemoved = affectedPlayers.stream()
                .filter(p -> !p.isOnline() || p.getWorld() != world || p.getLocation().distance(loc) > maxDistance)
                .collect(Collectors.toSet());
        toBeRemoved.forEach(p -> hide(p));
        affectedPlayers.removeAll(toBeRemoved);
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld() == world && p.getLocation().distance(loc) <= maxDistance)
                .filter(affectedPlayers::add).forEach(p -> show(p));
    }
    
    /**
     * Sends packets containing info about this creature to all players on the server (and to all new players).
     */
    public void spawn() {
        if(invalidated)
            throw new IllegalStateException(String.format("FakeCreature {%d} had been invalidated, it can't be spawned again!", getId()));
        if(isValid())
            throw new IllegalStateException(String.format("FakeCreature {%d} is already spawned!", getId()));
        AbstractPacket[] info = getInfo();
        Set<Player> players = new HashSet<>();
        players.addAll(affectedPlayers);
        players.forEach(p -> {
            for(AbstractPacket packet : info)
                packet.sendPacket(p);
        });
        updateRotation();
        tick();
        addAliveCreature(this);
        valid = true;
        affectedPlayers.forEach(this::onShowing);
    }
    
    /**
     * The same as invalidate().
     */
    public void delete() {
        invalidate();
    }
    
    /**
     * Despawns creature (if it's not despawned yet) and deletes all information about it.
     */
    public void invalidate() {
        if(invalidated)
            throw new IllegalStateException(String.format("FakeCreature {%d} is already invalidated!", getId()));
        if(isValid())
            despawn();
        FakeEntitiesManager.removeCreature(this);
        FakeCreaturesWorker.removeWorkingWith(this);
        invalidated = true;
    }
    
    public void despawn() {
        validate();
        valid = false;
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[]{getId()});
        affectedPlayers.forEach(p -> {
            destroy.sendPacket(p);
            onHiding(p);
        });
        affectedPlayers.clear();
        removeAliveCreature(this);
    }
    
    void updateRotation(Player p) {
        getRotationPacket().sendPacket(p);
    }
    
    void updateRotation() {
        AbstractPacket head = getRotationPacket();
        affectedPlayers.forEach(head::sendPacket);
    }
    
    AbstractPacket getRotationPacket() {
        WrapperPlayServerEntityHeadRotation head = new WrapperPlayServerEntityHeadRotation();
        head.setEntityID(id);
        head.setHeadYaw((byte) (getYaw() * 256.0f / 360.0f));
        return head;
    }
    
    public double getX() {
        return location.getX();
    }
    
    public double getY() {
        return location.getY();
    }
    
    public double getZ() {
        return location.getZ();
    }
    
    public float getYaw() {
        return location.getYaw();
    }
    
    public float getPitch() {
        return location.getPitch();
    }
    
    public void setX(double x) {
        location.setX(x);
        updatePosition();
    }
    
    public void setY(double y) {
        location.setY(y);
        updatePosition();
    }
    
    public void setZ(double z) {
        location.setZ(z);
        updatePosition();
    }
    
    public void setLocationWithoutUpdate(double x, double y, double z) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
    }
    
    public void setLocationWithoutUpdate(Location loc) {
        location.setX(loc.getX());
        location.setY(loc.getY());
        location.setZ(loc.getZ());
        location.setYaw(loc.getYaw());
        location.setPitch(loc.getPitch());
    }
    
    public void setLocation(double x, double y, double z) {
        setLocationWithoutUpdate(x, y, z);
        updatePosition();
    }
    
    public void setLocation(Location loc) {
        setLocationWithoutUpdate(loc);
        updatePosition();
    }
    
    public void changeLocation(double dx, double dy, double dz) {
        move(dx, dy, dz);
    }
    
    public void move(double dx, double dy, double dz) {
        location.setX(location.getX() + dx);
        location.setY(location.getY() + dy);
        location.setZ(location.getZ() + dz);
        updatePosition();
    }
    
    public void setYaw(float yaw) {
        location.setYaw(yaw);
        updatePosition();
    }
    
    public void setPitch(float pitch) {
        location.setPitch(pitch);
        updatePosition();
    }
    
    public void setRotation(float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);
        updatePosition();
    }
    
    public void changeRotation(float dy, float dp) {
        location.setYaw(location.getYaw() + dy);
        location.setPitch(location.getPitch() + dp);
        updatePosition();
    }
    
    private void updatePosition() {
        teleport(location);
    }
    
    public void lookAt(Entity e) {
        lookAt(e.getLocation());
    }
    
    public void lookAt(Location loc) {
        float[] angles = getRotationAngles(loc);
        setRotation(angles[0], angles[1]);
    }
    
    /**
     * Returns new yaw and pitch values (not their deltas).
     * @param e an entity.
     * @return new yaw & pitch values.
     */
    public float[] getRotationAngles(Entity e) {
        return getRotationAngles(e.getLocation());
    }
    
    /**
     * Returns new yaw and pitch values (not their deltas).
     * @param loc the location.
     * @return new yaw & pitch values.
     */
    public float[] getRotationAngles(Location loc) {
        Vector v = new Vector(loc.getX() - getX(), loc.getY() - getY(), loc.getZ() - getZ()).normalize();
        loc = getLocation().clone();
        loc.setDirection(v);
        return new float[]{loc.getYaw(), loc.getPitch()};
    }
    
    void teleport(Location location) {
        teleport(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    void teleport(double x, double y, double z, float yaw, float pitch) {
        validate();
        WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport();
        teleport.setEntityID(id);
        teleport.setYaw(yaw);
        teleport.setPitch(pitch);
        teleport.setX(x);
        teleport.setY(y);
        teleport.setZ(z);
        AbstractPacket head = getRotationPacket();
        affectedPlayers.forEach(p -> {
            teleport.sendPacket(p);
            head.sendPacket(p);
        });
    }
    
    public void playAnimationHand() {
        playAnimation(0);
    }
    
    public void playAnimationDamage() {
        playAnimation(1);
    }
    
    public void playAnimationHit() {
        playAnimation(4);
    }
    
    public void playAnimationCriticalHit() {
        playAnimation(5);
    }
    
    public void playAnimation(int animationId) {
        WrapperPlayServerAnimation wrapper = new WrapperPlayServerAnimation();
        wrapper.setEntityID(id);
        wrapper.setAnimation(animationId);
        affectedPlayers.forEach(wrapper::sendPacket);
    }
    
    public void playAnimationDeath() {
        WrapperPlayServerEntityStatus wrapper = new WrapperPlayServerEntityStatus();
        wrapper.setEntityID(id);
        wrapper.setEntityStatus((byte) 3);
        affectedPlayers.forEach(wrapper::sendPacket);
    }
    
    public void playSound(Sound s) {
        getWorld().playSound(getLocation(), s, 1f, 1f);
    }
    
    /**
     * Throws an exception whether npc is not valid.
     */
    public void validate() {
        if(!isValid())
            throw new IllegalStateException(String.format("FakeCreature {%d} is not valid!", getId()));
    }

}
