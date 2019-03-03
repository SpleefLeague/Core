/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBLoad;
import com.spleefleague.entitybuilder.DBLoadable;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;
import com.spleefleague.entitybuilder.TypeConverter.UUIDStringConverter;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
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
import org.bson.Document;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

/**
 *
 * @author Jonas
 */
public abstract class GeneralPlayer extends DBEntity implements DBLoadable, DBSaveable, Player {

    @DBLoad(fieldName = "username", priority = Integer.MAX_VALUE)
    private String username;
    @DBLoad(fieldName = "uuid", typeConverter = UUIDStringConverter.class, priority = Integer.MAX_VALUE)
    private UUID uuid;
    private Player cached;
    private final long created;
    @DBLoad(fieldName = "brokenProfiles")
    @DBSave(fieldName = "brokenProfiles")
    protected List<PlayerDataBackup> brokenProfiles;
    
    public GeneralPlayer() {
        this.created = System.currentTimeMillis();
    }
    
    public GeneralPlayer(UUID uuid, String username) {
        this();
        this.uuid = uuid;
        this.username = username;
    }
    
    protected void saveProfile(Document document) {
        if(brokenProfiles == null) {
            brokenProfiles = new ArrayList<>();
        }
        brokenProfiles.add(new PlayerDataBackup(document, new Date()));
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
    
    public int getPing() {
        return ((CraftPlayer)getPlayer()).getHandle().ping;
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
        return getPlayer().getDisplayName();
    }

    @Override
    public void setDisplayName(String name) {
        getPlayer().setDisplayName(name);
    }

    @Override
    public String getPlayerListName() {
        return getPlayer().getPlayerListName();
    }

    @Override
    public void setPlayerListName(String name) {
        getPlayer().setPlayerListName(name);
    }

    @Override
    public void setCompassTarget(Location loc) {
        getPlayer().setCompassTarget(loc);
    }

    @Override
    public Location getCompassTarget() {
        return getPlayer().getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return getPlayer().getAddress();
    }

    @Override
    public void sendRawMessage(String message) {
        getPlayer().sendRawMessage(message);
    }

    @Override
    public void kickPlayer(String message) {
        getPlayer().kickPlayer(message);
        this.cached = null;
    }

    @Override
    public void chat(String msg) {
        getPlayer().chat(msg);
    }

    @Override
    public boolean performCommand(String command) {
        return getPlayer().performCommand(command);
    }

    @Override
    public boolean isSneaking() {
        return getPlayer().isSneaking();
    }

    @Override
    public void setSneaking(boolean sneak) {
        getPlayer().setSneaking(sneak);
    }

    @Override
    public boolean isSprinting() {
        return getPlayer().isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        getPlayer().setSprinting(sprinting);
    }

    @Override
    public void saveData() {
        getPlayer().saveData();
    }

    @Override
    public void loadData() {
        getPlayer().loadData();
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        getPlayer().setSleepingIgnored(isSleeping);
    }

    @Override
    public boolean isSleepingIgnored() {
        return getPlayer().isSleepingIgnored();
    }

    @Override
    @Deprecated
    public void playNote(Location loc, byte instrument, byte note) {
        getPlayer().playNote(loc, instrument, note);
    }

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        getPlayer().playNote(loc, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        getPlayer().playSound(location, sound, volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        getPlayer().playSound(location, sound, volume, pitch);
    }

    @Override
    @Deprecated
    public void playEffect(Location loc, Effect effect, int data) {
        getPlayer().playEffect(loc, effect, data);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        getPlayer().playEffect(loc, effect, data);
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
        getPlayer().sendBlockChange(loc, material, data);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        return getPlayer().sendChunkChange(loc, sx, sy, sz, data);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException {
        getPlayer().sendSignChange(loc, lines);
    }

    @Override
    public void sendMap(MapView map2) {
        getPlayer().sendMap(map2);
    }

    @Override
    public void updateInventory() {
        getPlayer().updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        getPlayer().awardAchievement(achievement);
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        getPlayer().removeAchievement(achievement);
    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return getPlayer().hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        getPlayer().incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        getPlayer().decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        getPlayer().incrementStatistic(statistic, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        getPlayer().decrementStatistic(statistic, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
        getPlayer().setStatistic(statistic, newValue);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return getPlayer().getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        getPlayer().incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        getPlayer().decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return getPlayer().getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        getPlayer().incrementStatistic(statistic, material, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        getPlayer().decrementStatistic(statistic, material, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
        getPlayer().setStatistic(statistic, material, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        getPlayer().incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        getPlayer().decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return getPlayer().getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
        getPlayer().incrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        getPlayer().decrementStatistic(statistic, entityType, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        getPlayer().setStatistic(statistic, entityType, newValue);
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        getPlayer().setPlayerTime(time, relative);
    }

    @Override
    public long getPlayerTime() {
        return getPlayer().getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return getPlayer().getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return getPlayer().isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        getPlayer().resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(WeatherType type) {
        getPlayer().setPlayerWeather(type);
    }

    @Override
    public WeatherType getPlayerWeather() {
        return getPlayer().getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather() {
        getPlayer().resetPlayerWeather();
    }

    @Override
    public void giveExp(int amount) {
        getPlayer().giveExp(amount);
    }

    @Override
    public void giveExpLevels(int amount) {
        getPlayer().giveExpLevels(amount);
    }

    @Override
    public float getExp() {
        return getPlayer().getExp();
    }

    @Override
    public void setExp(float exp) {
        getPlayer().setExp(exp);
    }

    @Override
    public int getLevel() {
        return getPlayer().getLevel();
    }

    @Override
    public void setLevel(int level) {
        getPlayer().setLevel(level);
    }

    @Override
    public int getTotalExperience() {
        return getPlayer().getTotalExperience();
    }

    @Override
    public void setTotalExperience(int exp) {
        getPlayer().setTotalExperience(exp);
    }

    @Override
    public float getExhaustion() {
        return getPlayer().getExhaustion();
    }

    @Override
    public void setExhaustion(float value) {
        getPlayer().setExhaustion(value);
    }

    @Override
    public float getSaturation() {
        return getPlayer().getSaturation();
    }

    @Override
    public void setSaturation(float value) {
        getPlayer().setSaturation(value);
    }

    @Override
    public int getFoodLevel() {
        return getPlayer().getFoodLevel();
    }

    @Override
    public void setFoodLevel(int value) {
        getPlayer().setFoodLevel(value);
    }

    @Override
    public Location getBedSpawnLocation() {
        return getPlayer().getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        getPlayer().setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {
        getPlayer().setBedSpawnLocation(location, force);
    }

    @Override
    public boolean getAllowFlight() {
        return getPlayer().getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean flight) {
        getPlayer().setAllowFlight(flight);
    }

    @Override
    public void hidePlayer(Player player) {
        getPlayer().hidePlayer(player);
    }

    @Override
    public void showPlayer(Player player) {
        getPlayer().showPlayer(player);
    }

    @Override
    public boolean canSee(Player player) {
        return getPlayer().canSee(player);
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return getPlayer().isOnGround();
    }

    @Override
    public boolean isFlying() {
        return getPlayer().isFlying();
    }

    @Override
    public void setFlying(boolean value) {
        getPlayer().setFlying(value);
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        getPlayer().setFlySpeed(value);
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        getPlayer().setWalkSpeed(value);
    }

    @Override
    public float getFlySpeed() {
        return getPlayer().getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return getPlayer().getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(String url) {
        getPlayer().setTexturePack(url);
    }

    @Override
    public void setResourcePack(String url) {
        getPlayer().setResourcePack(url);
    }
    
    public void setResourcePack(String url, byte[] b) {
        getPlayer().setResourcePack(url, b);
    }

    @Override
    public Scoreboard getScoreboard() {
        return getPlayer().getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        getPlayer().setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled() {
        return getPlayer().isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean scale) {
        getPlayer().setHealthScaled(scale);
    }

    @Override
    public void setHealthScale(double scale) throws IllegalArgumentException {
        getPlayer().setHealthScale(scale);
    }

    public EntityPlayer getHandle() {
        return ((CraftPlayer) getPlayer()).getHandle();
    }

    @Override
    public double getHealthScale() {
        return getPlayer().getHealthScale();
    }

    @Override
    public Entity getSpectatorTarget() {
        return getPlayer().getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
        getPlayer().setSpectatorTarget(entity);
    }

    @Override
    @Deprecated
    public void sendTitle(String title, String subtitle) {
        getPlayer().sendTitle(title, subtitle);
    }

    @Override
    public void resetTitle() {
        getPlayer().resetTitle();
    }

    @Override
    public Player.Spigot spigot() {
        return getPlayer().spigot();
    }

    @Override
    public PlayerInventory getInventory() {
        return getPlayer().getInventory();
    }

    @Override
    public Inventory getEnderChest() {
        return getPlayer().getEnderChest();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        return getPlayer().setWindowProperty(prop, value);
    }

    @Override
    public InventoryView getOpenInventory() {
        return getPlayer().getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        return getPlayer().openInventory(inventory);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean force) {
        return getPlayer().openWorkbench(location, force);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean force) {
        return getPlayer().openEnchanting(location, force);
    }

    @Override
    public void openInventory(InventoryView inventory) {
        getPlayer().openInventory(inventory);
    }

    @Override
    public void closeInventory() {
        getPlayer().closeInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return getPlayer().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        getPlayer().setItemInHand(item);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return getPlayer().getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        getPlayer().setItemOnCursor(item);
    }

    @Override
    public boolean isSleeping() {
        return getPlayer().isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return getPlayer().getSleepTicks();
    }

    @Override
    public GameMode getGameMode() {
        return getPlayer().getGameMode();
    }

    @Override
    public void setGameMode(GameMode mode) {
        getPlayer().setGameMode(mode);
    }

    @Override
    public boolean isBlocking() {
        return getPlayer().isBlocking();
    }

    @Override
    public int getExpToLevel() {
        return getPlayer().getExpToLevel();
    }

    @Override
    public double getEyeHeight() {
        return getPlayer().getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        return getPlayer().getEyeHeight(ignoreSneaking);
    }

    @Override
    public Location getEyeLocation() {
        return getPlayer().getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return getPlayer().getLineOfSight(transparent, maxDistance);
    }

    @Override
    public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
        return getPlayer().getTargetBlock(transparent, maxDistance);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
        return getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    @Override
    public int getRemainingAir() {
        return getPlayer().getRemainingAir();
    }

    @Override
    public void setRemainingAir(int ticks) {
        getPlayer().setRemainingAir(ticks);
    }

    @Override
    public int getMaximumAir() {
        return getPlayer().getMaximumAir();
    }

    @Override
    public void setMaximumAir(int ticks) {
        getPlayer().setMaximumAir(ticks);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return getPlayer().getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
        getPlayer().setMaximumNoDamageTicks(ticks);
    }

    @Override
    public double getLastDamage() {
        return getPlayer().getLastDamage();
    }

    @Override
    public void setLastDamage(double damage) {
        getPlayer().setLastDamage(damage);
    }

    @Override
    public int getNoDamageTicks() {
        return getPlayer().getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        getPlayer().setNoDamageTicks(ticks);
    }

    @Override
    public Player getKiller() {
        return getPlayer().getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return getPlayer().addPotionEffect(effect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return getPlayer().addPotionEffect(effect, force);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return getPlayer().addPotionEffects(effects);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return getPlayer().hasPotionEffect(type);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        getPlayer().removePotionEffect(type);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return getPlayer().getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity other) {
        return getPlayer().hasLineOfSight(other);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return getPlayer().getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean remove) {
        getPlayer().setRemoveWhenFarAway(remove);
    }

    @Override
    public EntityEquipment getEquipment() {
        return getPlayer().getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean pickup) {
        getPlayer().setCanPickupItems(pickup);
    }

    @Override
    public boolean getCanPickupItems() {
        return getPlayer().getCanPickupItems();
    }

    @Override
    public boolean isLeashed() {
        return getPlayer().isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return getPlayer().getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity holder) {
        return getPlayer().setLeashHolder(holder);
    }

    @Override
    public Location getLocation() {
        return getPlayer().getLocation();
    }

    @Override
    public Location getLocation(Location loc) {
        return getPlayer().getLocation(loc);
    }

    @Override
    public void setVelocity(Vector velocity) {
        getPlayer().setVelocity(velocity);
    }

    @Override
    public Vector getVelocity() {
        return getPlayer().getVelocity();
    }

    @Override
    public World getWorld() {
        return getPlayer().getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return getPlayer().teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        return getPlayer().teleport(location, cause);
    }

    @Override
    public boolean teleport(Entity destination) {
        return getPlayer().teleport(destination);
    }

    @Override
    public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
        return getPlayer().teleport(destination, cause);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return getPlayer().getNearbyEntities(x, y, z);
    }

    @Override
    public int getEntityId() {
        return getPlayer().getEntityId();
    }

    @Override
    public int getFireTicks() {
        return getPlayer().getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return getPlayer().getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int ticks) {
        getPlayer().setFireTicks(ticks);
    }

    @Override
    public void remove() {
        getPlayer().remove();
    }

    @Override
    public boolean isDead() {
        return getPlayer().isDead();
    }

    @Override
    public boolean isValid() {
        return getPlayer().isValid();
    }

    @Override
    public Server getServer() {
        return getPlayer().getServer();
    }

    @Override
    public Entity getPassenger() {
        return getPlayer().getPassenger();
    }

    @Override
    public boolean setPassenger(Entity passenger) {
        return getPlayer().setPassenger(passenger);
    }

    @Override
    public boolean isEmpty() {
        return getPlayer().isEmpty();
    }

    @Override
    public boolean eject() {
        return getPlayer().eject();
    }

    @Override
    public float getFallDistance() {
        return getPlayer().getFallDistance();
    }

    @Override
    public void setFallDistance(float distance) {
        getPlayer().setFallDistance(distance);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        getPlayer().setLastDamageCause(event);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return getPlayer().getLastDamageCause();
    }

    @Override
    public int getTicksLived() {
        return getPlayer().getTicksLived();
    }

    @Override
    public void setTicksLived(int value) {
        getPlayer().setTicksLived(value);
    }

    @Override
    public void playEffect(EntityEffect type) {
        getPlayer().playEffect(type);
    }

    @Override
    public EntityType getType() {
        return getPlayer().getType();
    }

    @Override
    public boolean isInsideVehicle() {
        return getPlayer().isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return getPlayer().leaveVehicle();
    }

    @Override
    public Entity getVehicle() {
        return getPlayer().getVehicle();
    }

    @Override
    public void setCustomName(String name) {
        getPlayer().setCustomName(name);
    }

    @Override
    public String getCustomName() {
        return getPlayer().getCustomName();
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        getPlayer().setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return getPlayer().isCustomNameVisible();
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        getPlayer().setMetadata(metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return getPlayer().getMetadata(metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return getPlayer().hasMetadata(metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        getPlayer().removeMetadata(metadataKey, owningPlugin);
    }

    @Override
    public void sendMessage(String[] messages) {
        getPlayer().sendMessage(messages);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return getPlayer().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return getPlayer().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return getPlayer().hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return getPlayer().hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return getPlayer().addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return getPlayer().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return getPlayer().addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return getPlayer().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        getPlayer().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        getPlayer().recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getPlayer().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return getPlayer().isOp();
    }

    @Override
    public void setOp(boolean value) {
        getPlayer().setOp(value);
    }

    @Override
    public void damage(double amount) {
        getPlayer().damage(amount);
    }

    @Override
    public void damage(double amount, Entity source) {
        getPlayer().damage(amount, source);
    }

    @Override
    public double getHealth() {
        return getPlayer().getHealth();
    }

    @Override
    public void setHealth(double health) {
        getPlayer().setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return getPlayer().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        getPlayer().setMaxHealth(health);
    }

    @Override
    public void resetMaxHealth() {
        getPlayer().resetMaxHealth();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return (T) getPlayer().launchProjectile(projectile);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return (T) getPlayer().launchProjectile(projectile, velocity);
    }

    @Override
    public boolean isConversing() {
        return getPlayer().isConversing();
    }

    @Override
    public void acceptConversationInput(String input) {
        getPlayer().acceptConversationInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return getPlayer().beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        getPlayer().abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        getPlayer().abandonConversation(conversation, details);
    }

    @Override
    public boolean isBanned() {
        return getPlayer().isBanned();
    }

    @Override
    public void setWhitelisted(boolean value) {
        getPlayer().setWhitelisted(value);
    }

    @Override
    public boolean isWhitelisted() {
        return getPlayer().isWhitelisted();
    }

    @Override
    public long getFirstPlayed() {
        return getPlayer().getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return getPlayer().getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return getPlayer().hasPlayedBefore();
    }

    @Override
    public Map<String, Object> serialize() {
        return getPlayer().serialize();
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        getPlayer().sendPluginMessage(source, channel, message);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return getPlayer().getListeningPluginChannels();
    }

    @Override
    public void playSound(Location lctn, Sound sound, SoundCategory sc, float f, float f1) {
        getPlayer().playSound(lctn, sound, sc, f, f1);
    }

    @Override
    public void playSound(Location lctn, String string, SoundCategory sc, float f, float f1) {
        getPlayer().playSound(lctn, string, sc, f, f1);
    }

    @Override
    public void stopSound(Sound sound) {
        getPlayer().stopSound(sound);
    }

    @Override
    public void stopSound(String string) {
        getPlayer().stopSound(string);
    }

    @Override
    public void stopSound(Sound sound, SoundCategory sc) {
        getPlayer().stopSound(sound, sc);
    }

    @Override
    public void stopSound(String string, SoundCategory sc) {
        getPlayer().stopSound(string, sc);
    }

    @Override
    public void sendTitle(String string, String string1, int i, int i1, int i2) {
        getPlayer().sendTitle(string, string1, i, i1, i2);
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i) {
        getPlayer().spawnParticle(prtcl, lctn, i);
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i) {
        getPlayer().spawnParticle(prtcl, d, d1, d2, i);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, T t) {
        getPlayer().spawnParticle(prtcl, lctn, i, t);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, T t) {
        getPlayer().spawnParticle(prtcl, d, d1, d2, i, t);
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2) {
        getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2);
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5) {
        getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, T t) {
        getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, t);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, T t) {
        getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, t);
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3) {
        getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, d3);
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, d6);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3, T t) {
        getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, d3, t);
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6, T t) {
        getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, d6, t);
    }

    @Override
    public MainHand getMainHand() {
        return getPlayer().getMainHand();
    }

    @Override
    public InventoryView openMerchant(Villager vlgr, boolean bln) {
        return getPlayer().openMerchant(vlgr, bln);
    }

    @Override
    public InventoryView openMerchant(Merchant mrchnt, boolean bln) {
        return getPlayer().openMerchant(mrchnt, bln);
    }

    @Override
    public boolean isHandRaised() {
        return getPlayer().isHandRaised();
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType pet) {
        return getPlayer().getPotionEffect(pet);
    }

    @Override
    public boolean isGliding() {
        return getPlayer().isGliding();
    }

    @Override
    public void setGliding(boolean bln) {
        getPlayer().setGliding(bln);
    }

    @Override
    public void setAI(boolean bln) {
        getPlayer().setAI(bln);
    }

    @Override
    public boolean hasAI() {
        return getPlayer().hasAI();
    }

    @Override
    public void setCollidable(boolean bln) {
        getPlayer().setCollidable(bln);
    }

    @Override
    public boolean isCollidable() {
        return getPlayer().isCollidable();
    }

    @Override
    public AttributeInstance getAttribute(Attribute atrbt) {
        return getPlayer().getAttribute(atrbt);
    }

    @Override
    public List<Entity> getPassengers() {
        return getPlayer().getPassengers();
    }

    @Override
    public boolean addPassenger(Entity entity) {
        return getPlayer().addPassenger(entity);
    }

    @Override
    public boolean removePassenger(Entity entity) {
        return getPlayer().removePassenger(entity);
    }

    @Override
    public void setGlowing(boolean bln) {
        getPlayer().setGlowing(bln);
    }

    @Override
    public boolean isGlowing() {
        return getPlayer().isGlowing();
    }

    @Override
    public void setInvulnerable(boolean bln) {
        getPlayer().setInvulnerable(bln);
    }

    @Override
    public boolean isInvulnerable() {
        return getPlayer().isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return getPlayer().isSilent();
    }

    @Override
    public void setSilent(boolean bln) {
        getPlayer().setSilent(bln);
    }

    @Override
    public boolean hasGravity() {
        return getPlayer().hasGravity();
    }

    @Override
    public void setGravity(boolean bln) {
        getPlayer().setGravity(bln);
    }

    @Override
    public int getPortalCooldown() {
        return getPlayer().getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int i) {
        getPlayer().setPortalCooldown(i);
    }

    @Override
    public Set<String> getScoreboardTags() {
        return getPlayer().getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(String string) {
        return getPlayer().addScoreboardTag(string);
    }

    @Override
    public boolean removeScoreboardTag(String string) {
        return getPlayer().removeScoreboardTag(string);
    }

    @Override
    public boolean hasCooldown(Material mtrl) {
        return getPlayer().hasCooldown(mtrl);
    }

    @Override
    public int getCooldown(Material mtrl) {
        return getPlayer().getCooldown(mtrl);
    }

    @Override
    public void setCooldown(Material mtrl, int i) {
        getPlayer().setCooldown(mtrl, i);
    }

    @Override
    public double getHeight() {
        return getPlayer().getHeight();
    }

    @Override
    public double getWidth() {
        return getPlayer().getWidth();
    }

    @Override
    public AdvancementProgress getAdvancementProgress(Advancement a) {
        return getPlayer().getAdvancementProgress(a);
    }

    @Override
    public String getLocale() {
        return getPlayer().getLocale();
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityLeft() {
        return getPlayer().getShoulderEntityLeft();
    }

    @Override
    @Deprecated
    public void setShoulderEntityLeft(Entity entity) {
        getPlayer().setShoulderEntityLeft(entity);
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityRight() {
        return getPlayer().getShoulderEntityLeft();
    }

    @Override
    @Deprecated
    public void setShoulderEntityRight(Entity entity) {
        getPlayer().setShoulderEntityLeft(entity);
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return getPlayer().getPistonMoveReaction();
    }

    @Override
    public void sendBlockChange(Location lctn, BlockData bd) {
        getPlayer().sendBlockChange(lctn, bd);
    }

    @Override
    public void hidePlayer(Plugin plugin, Player player) {
        getPlayer().hidePlayer(plugin, player);
    }

    @Override
    public void showPlayer(Plugin plugin, Player player) {
        getPlayer().showPlayer(plugin, player);
    }

    @Override
    public boolean isSwimming() {
        return getPlayer().isSwimming();
    }

    @Override
    public void setSwimming(boolean bln) {
        getPlayer().setSwimming(bln);
    }

    @Override
    public String getPlayerListHeader() {
        return getPlayer().getPlayerListHeader();
    }

    @Override
    public String getPlayerListFooter() {
        return getPlayer().getPlayerListFooter();
    }

    @Override
    public void setPlayerListHeader(String string) {
        getPlayer().setPlayerListHeader(string);
    }

    @Override
    public void setPlayerListFooter(String string) {
        getPlayer().setPlayerListFooter(string);
    }

    @Override
    public void setPlayerListHeaderFooter(String string, String string1) {
        getPlayer().setPlayerListHeaderFooter(string, string1);
    }

    @Override
    public int getClientViewDistance() {
        return getPlayer().getClientViewDistance();
    }

    @Override
    public void updateCommands() {
        getPlayer().updateCommands();
    }

    @Override
    public boolean sleep(Location lctn, boolean bln) {
        return getPlayer().sleep(lctn, bln);
    }

    @Override
    public void wakeup(boolean bln) {
        getPlayer().wakeup(bln);
    }

    @Override
    public Location getBedLocation() {
        return getPlayer().getBedLocation();
    }

    @Override
    public boolean discoverRecipe(NamespacedKey nk) {
        return getPlayer().discoverRecipe(nk);
    }

    @Override
    public int discoverRecipes(Collection<NamespacedKey> clctn) {
        return getPlayer().discoverRecipes(clctn);
    }

    @Override
    public boolean undiscoverRecipe(NamespacedKey nk) {
        return getPlayer().undiscoverRecipe(nk);
    }

    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> clctn) {
        return getPlayer().undiscoverRecipes(clctn);
    }

    @Override
    public Block getTargetBlockExact(int i) {
        return getPlayer().getTargetBlockExact(i);
    }

    @Override
    public Block getTargetBlockExact(int i, FluidCollisionMode fcm) {
        return getPlayer().getTargetBlockExact(i, fcm);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double d) {
        return getPlayer().rayTraceBlocks(d);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double d, FluidCollisionMode fcm) {
        return getPlayer().rayTraceBlocks(d, fcm);
    }

    @Override
    public boolean isRiptiding() {
        return getPlayer().isRiptiding();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return getPlayer().getBoundingBox();
    }

    @Override
    @Deprecated
    public boolean isPersistent() {
        return getPlayer().isPersistent();
    }

    @Override
    @Deprecated
    public void setPersistent(boolean bln) {
        getPlayer().setPersistent(bln);
    }

    @Override
    public BlockFace getFacing() {
        return getPlayer().getFacing();
    }
}
