/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.io.*;
import com.spleefleague.core.io.TypeConverter.UUIDStringConverter;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.Attribute;
import java.net.InetSocketAddress;
import java.util.*;

/**
 *
 * @author Jonas
 */
public abstract class GeneralPlayer extends DBEntity implements DBLoadable, DBSaveable, Player {

    @DBLoad(fieldName = "username", priority = Integer.MIN_VALUE)
    private String username;
    @DBLoad(fieldName = "uuid", typeConverter = UUIDStringConverter.class, priority = Integer.MIN_VALUE)
    private UUID uuid;
    private Player cached;
    private final long created;

    public GeneralPlayer() {
        this.created = System.currentTimeMillis();
    }

    @DBSave(fieldName = "uuid", typeConverter = UUIDStringConverter.class)
    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @DBSave(fieldName = "username")
    @Override
    public String getName() {
        return username;
    }

    @DBSave(fieldName = "lookupUsername")
    public String getLookupUsername() {
        return username.toLowerCase();
    }

    @Override
    public Player getPlayer() {
        if (cached != null && cached.isOnline()) {
            return cached;
        } else {
            return cached = Bukkit.getPlayer(uuid);
        }
    }

    @Override
    public boolean isOnline() {
        Player p = getPlayer();
        return p != null && p.isOnline();
    }

    /**
     * Get the time that this player instance was created. Mainly for debug
     * purposes (e.g. for the ghost player issue).
     *
     * @return time in MS since epoch when this instance was created.
     */
    public long getCreated() {
        return created;
    }

    protected void setName(String username) {
        this.username = username;
    }

    protected void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    public static Player[] toBukkitPlayer(GeneralPlayer... players) {
        Player[] bplayers = new Player[players.length];
        for (int i = 0; i < players.length; i++) {
            bplayers[i] = players[i].getPlayer();
        }
        return bplayers;
    }

    public static Collection<Player> toBukkitPlayer(Collection<GeneralPlayer> players) {
        Collection<Player> list = new HashSet<>();
        for (GeneralPlayer gp : players) {
            list.add(gp.getPlayer());
        }
        return list;
    }

    public void setDefaults() {

    }

    //There has to be a better way
    @Override
    public String getDisplayName() {
        return this.getPlayer().getDisplayName();
    }

    @Override
    public void setDisplayName(String name) {
        this.getPlayer().setDisplayName(name);
    }

    @Override
    public String getPlayerListName() {
        return this.getPlayer().getPlayerListName();
    }

    @Override
    public void setPlayerListName(String name) {
        this.getPlayer().setPlayerListName(name);
    }

    @Override
    public void setCompassTarget(Location loc) {
        this.getPlayer().setCompassTarget(loc);
    }

    @Override
    public Location getCompassTarget() {
        return this.getPlayer().getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.getPlayer().getAddress();
    }

    @Override
    public void sendRawMessage(String message) {
        this.getPlayer().sendRawMessage(message);
    }

    @Override
    public void kickPlayer(String message) {
        this.getPlayer().kickPlayer(message);
        this.cached = null;
    }

    @Override
    public void chat(String msg) {
        this.getPlayer().chat(msg);
    }

    @Override
    public boolean performCommand(String command) {
        return this.getPlayer().performCommand(command);
    }

    @Override
    public boolean isSneaking() {
        return this.getPlayer().isSneaking();
    }

    @Override
    public void setSneaking(boolean sneak) {
        this.getPlayer().setSneaking(sneak);
    }

    @Override
    public boolean isSprinting() {
        return this.getPlayer().isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.getPlayer().setSprinting(sprinting);
    }

    @Override
    public void saveData() {
        this.getPlayer().saveData();
    }

    @Override
    public void loadData() {
        this.getPlayer().loadData();
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        this.getPlayer().setSleepingIgnored(isSleeping);
    }

    @Override
    public boolean isSleepingIgnored() {
        return this.getPlayer().isSleepingIgnored();
    }

    @Override
    @Deprecated
    public void playNote(Location loc, byte instrument, byte note) {
        this.getPlayer().playNote(loc, instrument, note);
    }

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        this.getPlayer().playNote(loc, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        this.getPlayer().playSound(location, sound, volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        this.getPlayer().playSound(location, sound, volume, pitch);
    }

    @Override
    @Deprecated
    public void playEffect(Location loc, Effect effect, int data) {
        this.getPlayer().playEffect(loc, effect, data);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        this.getPlayer().playEffect(loc, effect, data);
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
        this.getPlayer().sendBlockChange(loc, material, data);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        return this.getPlayer().sendChunkChange(loc, sx, sy, sz, data);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location loc, int material, byte data) {
        this.getPlayer().sendBlockChange(loc, material, data);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException {
        this.getPlayer().sendSignChange(loc, lines);
    }

    @Override
    public void sendMap(MapView map2) {
        this.getPlayer().sendMap(map2);
    }

    @Override
    public void updateInventory() {
        this.getPlayer().updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        this.getPlayer().awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        this.getPlayer().removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return this.getPlayer().hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
        this.getPlayer().setStatistic(statistic, newValue);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return this.getPlayer().getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return this.getPlayer().getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, material, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, material, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
        this.getPlayer().setStatistic(statistic, material, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return this.getPlayer().getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        this.getPlayer().decrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        this.getPlayer().setStatistic(statistic, entityType, newValue);
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        this.getPlayer().setPlayerTime(time, relative);
    }

    @Override
    public long getPlayerTime() {
        return this.getPlayer().getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return this.getPlayer().getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return this.getPlayer().isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        this.getPlayer().resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(WeatherType type) {
        this.getPlayer().setPlayerWeather(type);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return this.getPlayer().getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        this.getPlayer().resetPlayerWeather();
    }

    @Override
    public void giveExp(int amount) {
        this.getPlayer().giveExp(amount);
    }

    @Override
    public void giveExpLevels(int amount) {
        this.getPlayer().giveExpLevels(amount);
    }

    @Override
    public float getExp() {
        return this.getPlayer().getExp();
    }

    @Override
    public void setExp(float exp) {
        this.getPlayer().setExp(exp);
    }

    @Override
    public int getLevel() {
        return this.getPlayer().getLevel();
    }

    @Override
    public void setLevel(int level) {
        this.getPlayer().setLevel(level);
    }

    @Override
    public int getTotalExperience() {
        return this.getPlayer().getTotalExperience();
    }

    @Override
    public void setTotalExperience(int exp) {
        this.getPlayer().setTotalExperience(exp);
    }

    @Override
    public float getExhaustion() {
        return this.getPlayer().getExhaustion();
    }

    @Override
    public void setExhaustion(float value) {
        this.getPlayer().setExhaustion(value);
    }

    @Override
    public float getSaturation() {
        return this.getPlayer().getSaturation();
    }

    @Override
    public void setSaturation(float value) {
        this.getPlayer().setSaturation(value);
    }

    @Override
    public int getFoodLevel() {
        return this.getPlayer().getFoodLevel();
    }

    @Override
    public void setFoodLevel(int value) {
        this.getPlayer().setFoodLevel(value);
    }

    @Override
    public Location getBedSpawnLocation() {
        return this.getPlayer().getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        this.getPlayer().setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {
        this.getPlayer().setBedSpawnLocation(location, force);
    }

    @Override
    public boolean getAllowFlight() {
        return this.getPlayer().getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean flight) {
        this.getPlayer().setAllowFlight(flight);
    }

    @Override
    public void hidePlayer(Player player) {
        this.getPlayer().hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        this.getPlayer().showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return this.getPlayer().canSee(player);
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return this.getPlayer().isOnGround();
    }

    @Override
    public boolean isFlying() {
        return this.getPlayer().isFlying();
    }

    @Override
    public void setFlying(boolean value) {
        this.getPlayer().setFlying(value);
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        this.getPlayer().setFlySpeed(value);
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        this.getPlayer().setWalkSpeed(value);
    }

    @Override
    public float getFlySpeed() {
        return this.getPlayer().getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return this.getPlayer().getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(String url) {
        this.getPlayer().setTexturePack(url);
    }

    @Override
    public void setResourcePack(String url) {
        this.getPlayer().setResourcePack(url);
    }
    
    public void setResourcePack(String url, byte[] b) {
        this.getPlayer().setResourcePack(url, b);
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.getPlayer().getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        this.getPlayer().setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled() {
        return this.getPlayer().isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean scale) {
        this.getPlayer().setHealthScaled(scale);
    }

    @Override
    public void setHealthScale(double scale) throws IllegalArgumentException {
        this.getPlayer().setHealthScale(scale);
    }

    public EntityPlayer getHandle() {
        return ((CraftPlayer) this.getPlayer()).getHandle();
    }

    @Override
    public double getHealthScale() {
        return this.getPlayer().getHealthScale();
    }

    @Override
    public Entity getSpectatorTarget() {
        return this.getPlayer().getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
        this.getPlayer().setSpectatorTarget(entity);
    }

    @Override
    @Deprecated
    public void sendTitle(String title, String subtitle) {
        this.getPlayer().sendTitle(title, subtitle);
    }

    @Override
    public void resetTitle() {
        this.getPlayer().resetTitle();
    }

    @Override
    public Player.Spigot spigot() {
        return this.getPlayer().spigot();
    }

    @Override
    public PlayerInventory getInventory() {
        return this.getPlayer().getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return this.getPlayer().getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        return this.getPlayer().setWindowProperty(prop, value);
    }

    @Override
    public InventoryView getOpenInventory() {
        return this.getPlayer().getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        return this.getPlayer().openInventory(inventory);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean force) {
        return this.getPlayer().openWorkbench(location, force);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean force) {
        return this.getPlayer().openEnchanting(location, force);
    }

    @Override
    public void openInventory(InventoryView inventory) {
        this.getPlayer().openInventory(inventory);
    }

    @Override
    public void closeInventory() {
        this.getPlayer().closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return this.getPlayer().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        this.getPlayer().setItemInHand(item);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return this.getPlayer().getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        this.getPlayer().setItemOnCursor(item);
    }

    @Override
    public boolean isSleeping() {
        return this.getPlayer().isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return this.getPlayer().getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return this.getPlayer().getGameMode();
    }

    @Override
    public void setGameMode(GameMode mode) {
        this.getPlayer().setGameMode(mode);
    }

    @Override
    public boolean isBlocking() {
        return this.getPlayer().isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return this.getPlayer().getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return this.getPlayer().getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        return this.getPlayer().getEyeHeight(ignoreSneaking);
    }

    @Override
    public Location getEyeLocation() {
        return this.getPlayer().getEyeLocation();
    }

    @Override
    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return this.getPlayer().getLineOfSight(transparent, maxDistance);
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return this.getPlayer().getLineOfSight(transparent, maxDistance);
    }

    @Override
    @Deprecated
    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        return this.getPlayer().getTargetBlock(transparent, maxDistance);
    }

    @Override
    public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
        return this.getPlayer().getTargetBlock(transparent, maxDistance);
    }

    @Override
    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        return this.getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
        return this.getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    @Override
    public int getRemainingAir() {
        return this.getPlayer().getRemainingAir();
    }

    @Override
    public void setRemainingAir(int ticks) {
        this.getPlayer().setRemainingAir(ticks);
    }

    @Override
    public int getMaximumAir() {
        return this.getPlayer().getMaximumAir();
    }

    @Override
    public void setMaximumAir(int ticks) {
        this.getPlayer().setMaximumAir(ticks);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return this.getPlayer().getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
        this.getPlayer().setMaximumNoDamageTicks(ticks);
    }

    @Override
    public double getLastDamage() {
        return this.getPlayer().getLastDamage();
    }

    @Override
    @Deprecated
    public int _INVALID_getLastDamage() {
        return this.getPlayer()._INVALID_getLastDamage();
    }

    @Override
    public void setLastDamage(double damage) {
        this.getPlayer().setLastDamage(damage);
    }

    @Override
    @Deprecated
    public void _INVALID_setLastDamage(int damage) {
        this.getPlayer()._INVALID_setLastDamage(damage);
    }

    @Override
    public int getNoDamageTicks() {
        return this.getPlayer().getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        this.getPlayer().setNoDamageTicks(ticks);
    }

    @Override
    public Player getKiller() {
        return this.getPlayer().getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return this.getPlayer().addPotionEffect(effect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return this.getPlayer().addPotionEffect(effect, force);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return this.getPlayer().addPotionEffects(effects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return this.getPlayer().hasPotionEffect(type);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        this.getPlayer().removePotionEffect(type);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return this.getPlayer().getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity other) {
        return this.getPlayer().hasLineOfSight(other);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return this.getPlayer().getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean remove) {
        this.getPlayer().setRemoveWhenFarAway(remove);
    }

    @Override
    public EntityEquipment getEquipment() {
        return this.getPlayer().getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean pickup) {
        this.getPlayer().setCanPickupItems(pickup);
    }

    @Override
    public boolean getCanPickupItems() {
        return this.getPlayer().getCanPickupItems();
    }

    @Override
    public boolean isLeashed() {
        return this.getPlayer().isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return this.getPlayer().getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity holder) {
        return this.getPlayer().setLeashHolder(holder);
    }

    @Override
    public Location getLocation() {
        return this.getPlayer().getLocation();
    }

    @Override
    public Location getLocation(Location loc) {
        return this.getPlayer().getLocation(loc);
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.getPlayer().setVelocity(velocity);
    }

    @Override
    public Vector getVelocity() {
        return this.getPlayer().getVelocity();
    }

    @Override
    public World getWorld() {
        return this.getPlayer().getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return this.getPlayer().teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        return this.getPlayer().teleport(location, cause);
    }

    @Override
    public boolean teleport(Entity destination) {
        return this.getPlayer().teleport(destination);
    }

    @Override
    public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
        return this.getPlayer().teleport(destination, cause);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return this.getPlayer().getNearbyEntities(x, y, z);
    }

    @Override
    public int getEntityId() {
        return this.getPlayer().getEntityId();
    }

    @Override
    public int getFireTicks() {
        return this.getPlayer().getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return this.getPlayer().getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int ticks) {
        this.getPlayer().setFireTicks(ticks);
    }

    @Override
    public void remove() {
        this.getPlayer().remove();
    }

    @Override
    public boolean isDead() {
        return this.getPlayer().isDead();
    }

    @Override
    public boolean isValid() {
        return this.getPlayer().isValid();
    }

    @Override
    public Server getServer() {
        return this.getPlayer().getServer();
    }

    @Override
    public Entity getPassenger() {
        return this.getPlayer().getPassenger();
    }

    @Override
    public boolean setPassenger(Entity passenger) {
        return this.getPlayer().setPassenger(passenger);
    }

    @Override
    public boolean isEmpty() {
        return this.getPlayer().isEmpty();
    }

    @Override
    public boolean eject() {
        return this.getPlayer().eject();
    }

    @Override
    public float getFallDistance() {
        return this.getPlayer().getFallDistance();
    }

    @Override
    public void setFallDistance(float distance) {
        this.getPlayer().setFallDistance(distance);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        this.getPlayer().setLastDamageCause(event);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return this.getPlayer().getLastDamageCause();
    }

    @Override
    public int getTicksLived() {
        return this.getPlayer().getTicksLived();
    }

    @Override
    public void setTicksLived(int value) {
        this.getPlayer().setTicksLived(value);
    }

    @Override
    public void playEffect(EntityEffect type) {
        this.getPlayer().playEffect(type);
    }

    @Override
    public EntityType getType() {
        return this.getPlayer().getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return this.getPlayer().isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return this.getPlayer().leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return this.getPlayer().getVehicle();
    }

    @Override
    public void setCustomName(String name) {
        this.getPlayer().setCustomName(name);
    }

    @Override
    public String getCustomName() {
        return this.getPlayer().getCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        this.getPlayer().setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.getPlayer().isCustomNameVisible();
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.getPlayer().setMetadata(metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.getPlayer().getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.getPlayer().hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.getPlayer().removeMetadata(metadataKey, owningPlugin);
    }

    @Override
    public void sendMessage(String[] messages) {
        this.getPlayer().sendMessage(messages);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return this.getPlayer().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return this.getPlayer().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return this.getPlayer().hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return this.getPlayer().hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return this.getPlayer().addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.getPlayer().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return this.getPlayer().addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return this.getPlayer().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.getPlayer().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.getPlayer().recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.getPlayer().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return this.getPlayer().isOp();
    }

    @Override
    public void setOp(boolean value) {
        this.getPlayer().setOp(value);
    }

    @Override
    public void damage(double amount) {
        this.getPlayer().damage(amount);
    }

    @Override
    @Deprecated
    public void _INVALID_damage(int amount) {
        this.getPlayer()._INVALID_damage(amount);
    }

    @Override
    public void damage(double amount, Entity source) {
        this.getPlayer().damage(amount, source);
    }

    @Override
    @Deprecated
    public void _INVALID_damage(int amount, Entity source) {
        this.getPlayer()._INVALID_damage(amount, source);
    }

    @Override
    public double getHealth() {
        return this.getPlayer().getHealth();
    }

    @Override
    @Deprecated
    public int _INVALID_getHealth() {
        return this.getPlayer()._INVALID_getHealth();
    }

    @Override
    public void setHealth(double health) {
        this.getPlayer().setHealth(health);
    }

    @Override
    public void _INVALID_setHealth(int health) {
        this.getPlayer()._INVALID_setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return this.getPlayer().getMaxHealth();
    }

    @Override
    @Deprecated
    public int _INVALID_getMaxHealth() {
        return this.getPlayer()._INVALID_getMaxHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        this.getPlayer().setMaxHealth(health);
    }

    @Override
    @Deprecated
    public void _INVALID_setMaxHealth(int health) {
        this.getPlayer()._INVALID_setMaxHealth(health);
    }

    @Override
    public void resetMaxHealth() {
        this.getPlayer().resetMaxHealth();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return (T) this.getPlayer().launchProjectile(projectile);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return (T) this.getPlayer().launchProjectile(projectile, velocity);
    }

    @Override
    public boolean isConversing() {
        return this.getPlayer().isConversing();
    }

    @Override
    public void acceptConversationInput(String input) {
        this.getPlayer().acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return this.getPlayer().beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        this.getPlayer().abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        this.getPlayer().abandonConversation(conversation, details);
    }

    @Override
    public boolean isBanned() {
        return this.getPlayer().isBanned();
    }

    @Override
    @Deprecated
    public void setBanned(boolean banned) {
        this.getPlayer().setBanned(banned);
    }

    @Override
    public boolean isWhitelisted() {
        return this.getPlayer().isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean value) {
        this.getPlayer().setWhitelisted(value);
    }

    @Override
    public long getFirstPlayed() {
        return this.getPlayer().getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return this.getPlayer().getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.getPlayer().hasPlayedBefore();
    }

    @Override
    public Map<String, Object> serialize() {
        return this.getPlayer().serialize();
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        this.getPlayer().sendPluginMessage(source, channel, message);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return this.getPlayer().getListeningPluginChannels();
    }

    @Override
    public void playSound(Location lctn, Sound sound, SoundCategory sc, float f, float f1) {
        this.getPlayer().playSound(lctn, sound, sc, f, f1);
    }

    @Override
    public void playSound(Location lctn, String string, SoundCategory sc, float f, float f1) {
        this.getPlayer().playSound(lctn, string, sc, f, f1);
    }

    @Override
    public void stopSound(Sound sound) {
        this.getPlayer().stopSound(sound);
    }

    @Override
    public void stopSound(String string) {
        this.getPlayer().stopSound(string);
    }

    @Override
    public void stopSound(Sound sound, SoundCategory sc) {
        this.getPlayer().stopSound(sound, sc);
    }

    @Override
    public void stopSound(String string, SoundCategory sc) {
        this.getPlayer().stopSound(string, sc);
    }

    @Override
    public void sendTitle(String string, String string1, int i, int i1, int i2) {
        this.getPlayer().sendTitle(string, string1, i, i1, i2);
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i) {
        this.getPlayer().spawnParticle(prtcl, lctn, i);
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, T t) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, t);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, T t) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, t);
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2);
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, T t) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, t);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, T t) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, t);
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, d3);
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, d6);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3, T t) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, d3, t);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6, T t) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, d6, t);
    }

    @Override
    public MainHand getMainHand() {
        return this.getPlayer().getMainHand();
    }

    @Override
    public InventoryView openMerchant(Villager vlgr, boolean bln) {
        return this.getPlayer().openMerchant(vlgr, bln);
    }

    @Override
    public InventoryView openMerchant(Merchant mrchnt, boolean bln) {
        return this.getPlayer().openMerchant(mrchnt, bln);
    }

    @Override
    public boolean isHandRaised() {
        return this.getPlayer().isHandRaised();
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType pet) {
        return this.getPlayer().getPotionEffect(pet);
    }

    @Override
    public boolean isGliding() {
        return this.getPlayer().isGliding();
    }

    @Override
    public void setGliding(boolean bln) {
        this.getPlayer().setGliding(bln);
    }

    @Override
    public void setAI(boolean bln) {
        this.getPlayer().setAI(bln);
    }

    @Override
    public boolean hasAI() {
        return this.getPlayer().hasAI();
    }

    @Override
    public void setCollidable(boolean bln) {
        this.getPlayer().setCollidable(bln);
    }

    @Override
    public boolean isCollidable() {
        return this.getPlayer().isCollidable();
    }

    @Override
    public AttributeInstance getAttribute(Attribute atrbt) {
        return this.getPlayer().getAttribute(atrbt);
    }

    @Override
    public List<Entity> getPassengers() {
        return this.getPlayer().getPassengers();
    }

    @Override
    public boolean addPassenger(Entity entity) {
        return this.getPlayer().addPassenger(entity);
    }

    @Override
    public boolean removePassenger(Entity entity) {
        return this.getPlayer().removePassenger(entity);
    }

    @Override
    public void setGlowing(boolean bln) {
        this.getPlayer().setGlowing(bln);
    }

    @Override
    public boolean isGlowing() {
        return this.getPlayer().isGlowing();
    }

    @Override
    public void setInvulnerable(boolean bln) {
        this.getPlayer().setInvulnerable(bln);
    }

    @Override
    public boolean isInvulnerable() {
        return this.getPlayer().isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return this.getPlayer().isSilent();
    }

    @Override
    public void setSilent(boolean bln) {
        this.getPlayer().setSilent(bln);
    }

    @Override
    public boolean hasGravity() {
        return this.getPlayer().hasGravity();
    }

    @Override
    public void setGravity(boolean bln) {
        this.getPlayer().setGravity(bln);
    }

    @Override
    public int getPortalCooldown() {
        return this.getPlayer().getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int i) {
        this.getPlayer().setPortalCooldown(i);
    }

    @Override
    public Set<String> getScoreboardTags() {
        return this.getPlayer().getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(String string) {
        return this.getPlayer().addScoreboardTag(string);
    }

    @Override
    public boolean removeScoreboardTag(String string) {
        return this.getPlayer().removeScoreboardTag(string);
    }
}
