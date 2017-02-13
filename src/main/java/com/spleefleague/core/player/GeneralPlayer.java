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

    @DBLoad(fieldName = "username", priority = Integer.MAX_VALUE)
    private String username;
    @DBLoad(fieldName = "uuid", typeConverter = UUIDStringConverter.class, priority = Integer.MAX_VALUE)
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
    public String getDisplayName() {
        return this.getPlayer().getDisplayName();
    }

    public void setDisplayName(String name) {
        this.getPlayer().setDisplayName(name);
    }

    public String getPlayerListName() {
        return this.getPlayer().getPlayerListName();
    }

    public void setPlayerListName(String name) {
        this.getPlayer().setPlayerListName(name);
    }

    public void setCompassTarget(Location loc) {
        this.getPlayer().setCompassTarget(loc);
    }

    public Location getCompassTarget() {
        return this.getPlayer().getCompassTarget();
    }

    public InetSocketAddress getAddress() {
        return this.getPlayer().getAddress();
    }

    public void sendRawMessage(String message) {
        this.getPlayer().sendRawMessage(message);
    }

    public void kickPlayer(String message) {
        this.getPlayer().kickPlayer(message);
        this.cached = null;
    }

    public void chat(String msg) {
        this.getPlayer().chat(msg);
    }

    public boolean performCommand(String command) {
        return this.getPlayer().performCommand(command);
    }

    public boolean isSneaking() {
        return this.getPlayer().isSneaking();
    }

    public void setSneaking(boolean sneak) {
        this.getPlayer().setSneaking(sneak);
    }

    public boolean isSprinting() {
        return this.getPlayer().isSprinting();
    }

    public void setSprinting(boolean sprinting) {
        this.getPlayer().setSprinting(sprinting);
    }

    public void saveData() {
        this.getPlayer().saveData();
    }

    public void loadData() {
        this.getPlayer().loadData();
    }

    public void setSleepingIgnored(boolean isSleeping) {
        this.getPlayer().setSleepingIgnored(isSleeping);
    }

    public boolean isSleepingIgnored() {
        return this.getPlayer().isSleepingIgnored();
    }

    @Deprecated
    public void playNote(Location loc, byte instrument, byte note) {
        this.getPlayer().playNote(loc, instrument, note);
    }

    public void playNote(Location loc, Instrument instrument, Note note) {
        this.getPlayer().playNote(loc, instrument, note);
    }

    public void playSound(Location location, Sound sound, float volume, float pitch) {
        this.getPlayer().playSound(location, sound, volume, pitch);
    }

    public void playSound(Location location, String sound, float volume, float pitch) {
        this.getPlayer().playSound(location, sound, volume, pitch);
    }

    @Deprecated
    public void playEffect(Location loc, Effect effect, int data) {
        this.getPlayer().playEffect(loc, effect, data);
    }

    public <T> void playEffect(Location loc, Effect effect, T data) {
        this.getPlayer().playEffect(loc, effect, data);
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        this.getPlayer().sendBlockChange(loc, material, data);
    }

    @Deprecated
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        return this.getPlayer().sendChunkChange(loc, sx, sy, sz, data);
    }

    @Deprecated
    public void sendBlockChange(Location loc, int material, byte data) {
        this.getPlayer().sendBlockChange(loc, material, data);
    }

    public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException {
        this.getPlayer().sendSignChange(loc, lines);
    }

    public void sendMap(MapView map2) {
        this.getPlayer().sendMap(map2);
    }

    public void updateInventory() {
        this.getPlayer().updateInventory();
    }

    public void awardAchievement(Achievement achievement) {
        this.getPlayer().awardAchievement(achievement);
    }

    public void removeAchievement(Achievement achievement) {
        this.getPlayer().removeAchievement(achievement);
    }

    public boolean hasAchievement(Achievement achievement) {
        return this.getPlayer().hasAchievement(achievement);
    }

    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic);
    }

    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic);
    }

    public void incrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, amount);
    }

    public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, amount);
    }

    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
        this.getPlayer().setStatistic(statistic, newValue);
    }

    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return this.getPlayer().getStatistic(statistic);
    }

    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, material);
    }

    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, material);
    }

    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return this.getPlayer().getStatistic(statistic, material);
    }

    public void incrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, material, amount);
    }

    public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, material, amount);
    }

    public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
        this.getPlayer().setStatistic(statistic, material, newValue);
    }

    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, entityType);
    }

    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        this.getPlayer().decrementStatistic(statistic, entityType);
    }

    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return this.getPlayer().getStatistic(statistic, entityType);
    }

    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
        this.getPlayer().incrementStatistic(statistic, entityType, amount);
    }

    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        this.getPlayer().decrementStatistic(statistic, entityType, amount);
    }

    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        this.getPlayer().setStatistic(statistic, entityType, newValue);
    }

    public void setPlayerTime(long time, boolean relative) {
        this.getPlayer().setPlayerTime(time, relative);
    }

    public long getPlayerTime() {
        return this.getPlayer().getPlayerTime();
    }

    public long getPlayerTimeOffset() {
        return this.getPlayer().getPlayerTimeOffset();
    }

    public boolean isPlayerTimeRelative() {
        return this.getPlayer().isPlayerTimeRelative();
    }

    public void resetPlayerTime() {
        this.getPlayer().resetPlayerTime();
    }

    public void setPlayerWeather(WeatherType type) {
        this.getPlayer().setPlayerWeather(type);
    }

    public WeatherType getPlayerWeather() {
        return this.getPlayer().getPlayerWeather();
    }

    public void resetPlayerWeather() {
        this.getPlayer().resetPlayerWeather();
    }

    public void giveExp(int amount) {
        this.getPlayer().giveExp(amount);
    }

    public void giveExpLevels(int amount) {
        this.getPlayer().giveExpLevels(amount);
    }

    public float getExp() {
        return this.getPlayer().getExp();
    }

    public void setExp(float exp) {
        this.getPlayer().setExp(exp);
    }

    public int getLevel() {
        return this.getPlayer().getLevel();
    }

    public void setLevel(int level) {
        this.getPlayer().setLevel(level);
    }

    public int getTotalExperience() {
        return this.getPlayer().getTotalExperience();
    }

    public void setTotalExperience(int exp) {
        this.getPlayer().setTotalExperience(exp);
    }

    public float getExhaustion() {
        return this.getPlayer().getExhaustion();
    }

    public void setExhaustion(float value) {
        this.getPlayer().setExhaustion(value);
    }

    public float getSaturation() {
        return this.getPlayer().getSaturation();
    }

    public void setSaturation(float value) {
        this.getPlayer().setSaturation(value);
    }

    public int getFoodLevel() {
        return this.getPlayer().getFoodLevel();
    }

    public void setFoodLevel(int value) {
        this.getPlayer().setFoodLevel(value);
    }

    public Location getBedSpawnLocation() {
        return this.getPlayer().getBedSpawnLocation();
    }

    public void setBedSpawnLocation(Location location) {
        this.getPlayer().setBedSpawnLocation(location);
    }

    public void setBedSpawnLocation(Location location, boolean force) {
        this.getPlayer().setBedSpawnLocation(location, force);
    }

    public boolean getAllowFlight() {
        return this.getPlayer().getAllowFlight();
    }

    public void setAllowFlight(boolean flight) {
        this.getPlayer().setAllowFlight(flight);
    }

    public void hidePlayer(Player player) {
        this.getPlayer().hidePlayer(player);
    }

    public void showPlayer(Player player) {
        this.getPlayer().showPlayer(player);
    }

    public boolean canSee(Player player) {
        return this.getPlayer().canSee(player);
    }

    public void hidePlayer(GeneralPlayer player) {
        this.getPlayer().hidePlayer(player.getPlayer());
    }

    public void showPlayer(GeneralPlayer player) {
        this.getPlayer().showPlayer(player.getPlayer());
    }

    public boolean canSee(GeneralPlayer player) {
        return this.getPlayer().canSee(player.getPlayer());
    }

    @Deprecated
    public boolean isOnGround() {
        return this.getPlayer().isOnGround();
    }

    public boolean isFlying() {
        return this.getPlayer().isFlying();
    }

    public void setFlying(boolean value) {
        this.getPlayer().setFlying(value);
    }

    public void setFlySpeed(float value) throws IllegalArgumentException {
        this.getPlayer().setFlySpeed(value);
    }

    public void setWalkSpeed(float value) throws IllegalArgumentException {
        this.getPlayer().setWalkSpeed(value);
    }

    public float getFlySpeed() {
        return this.getPlayer().getFlySpeed();
    }

    public float getWalkSpeed() {
        return this.getPlayer().getWalkSpeed();
    }

    @Deprecated
    public void setTexturePack(String url) {
        this.getPlayer().setTexturePack(url);
    }

    public void setResourcePack(String url) {
        this.getPlayer().setResourcePack(url);
    }

    public Scoreboard getScoreboard() {
        return this.getPlayer().getScoreboard();
    }

    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        this.getPlayer().setScoreboard(scoreboard);
    }

    public boolean isHealthScaled() {
        return this.getPlayer().isHealthScaled();
    }

    public void setHealthScaled(boolean scale) {
        this.getPlayer().setHealthScaled(scale);
    }

    public void setHealthScale(double scale) throws IllegalArgumentException {
        this.getPlayer().setHealthScale(scale);
    }

    public EntityPlayer getHandle() {
        return ((CraftPlayer)this.getPlayer()).getHandle();
    }

    public double getHealthScale() {
        return this.getPlayer().getHealthScale();
    }

    public Entity getSpectatorTarget() {
        return this.getPlayer().getSpectatorTarget();
    }

    public void setSpectatorTarget(Entity entity) {
        this.getPlayer().setSpectatorTarget(entity);
    }

    @Deprecated
    public void sendTitle(String title, String subtitle) {
        this.getPlayer().sendTitle(title, subtitle);
    }

    public void resetTitle() {
        this.getPlayer().resetTitle();
    }

    public Player.Spigot spigot() {
        return this.getPlayer().spigot();
    }

    public PlayerInventory getInventory() {
        return this.getPlayer().getInventory();
    }

    public Inventory getEnderChest() {
        return this.getPlayer().getEnderChest();
    }

    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        return this.getPlayer().setWindowProperty(prop, value);
    }

    public InventoryView getOpenInventory() {
        return this.getPlayer().getOpenInventory();
    }

    public InventoryView openInventory(Inventory inventory) {
        return this.getPlayer().openInventory(inventory);
    }

    public InventoryView openWorkbench(Location location, boolean force) {
        return this.getPlayer().openWorkbench(location, force);
    }

    public InventoryView openEnchanting(Location location, boolean force) {
        return this.getPlayer().openEnchanting(location, force);
    }

    public void openInventory(InventoryView inventory) {
        this.getPlayer().openInventory(inventory);
    }

    public void closeInventory() {
        this.getPlayer().closeInventory();
    }

    public ItemStack getItemInHand() {
        return this.getPlayer().getItemInHand();
    }

    public void setItemInHand(ItemStack item) {
        this.getPlayer().setItemInHand(item);
    }

    public ItemStack getItemOnCursor() {
        return this.getPlayer().getItemOnCursor();
    }

    public void setItemOnCursor(ItemStack item) {
        this.getPlayer().setItemOnCursor(item);
    }

    public boolean isSleeping() {
        return this.getPlayer().isSleeping();
    }

    public int getSleepTicks() {
        return this.getPlayer().getSleepTicks();
    }

    public GameMode getGameMode() {
        return this.getPlayer().getGameMode();
    }

    public void setGameMode(GameMode mode) {
        this.getPlayer().setGameMode(mode);
    }

    public boolean isBlocking() {
        return this.getPlayer().isBlocking();
    }

    public int getExpToLevel() {
        return this.getPlayer().getExpToLevel();
    }

    public double getEyeHeight() {
        return this.getPlayer().getEyeHeight();
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        return this.getPlayer().getEyeHeight(ignoreSneaking);
    }

    public Location getEyeLocation() {
        return this.getPlayer().getEyeLocation();
    }

    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return this.getPlayer().getLineOfSight(transparent, maxDistance);
    }

    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return this.getPlayer().getLineOfSight(transparent, maxDistance);
    }

    @Deprecated
    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        return this.getPlayer().getTargetBlock(transparent, maxDistance);
    }

    public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
        return this.getPlayer().getTargetBlock(transparent, maxDistance);
    }

    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        return this.getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
        return this.getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    public int getRemainingAir() {
        return this.getPlayer().getRemainingAir();
    }

    public void setRemainingAir(int ticks) {
        this.getPlayer().setRemainingAir(ticks);
    }

    public int getMaximumAir() {
        return this.getPlayer().getMaximumAir();
    }

    public void setMaximumAir(int ticks) {
        this.getPlayer().setMaximumAir(ticks);
    }

    public int getMaximumNoDamageTicks() {
        return this.getPlayer().getMaximumNoDamageTicks();
    }

    public void setMaximumNoDamageTicks(int ticks) {
        this.getPlayer().setMaximumNoDamageTicks(ticks);
    }

    public double getLastDamage() {
        return this.getPlayer().getLastDamage();
    }

    @Deprecated
    public int _INVALID_getLastDamage() {
        return this.getPlayer()._INVALID_getLastDamage();
    }

    public void setLastDamage(double damage) {
        this.getPlayer().setLastDamage(damage);
    }

    @Deprecated
    public void _INVALID_setLastDamage(int damage) {
        this.getPlayer()._INVALID_setLastDamage(damage);
    }

    public int getNoDamageTicks() {
        return this.getPlayer().getNoDamageTicks();
    }

    public void setNoDamageTicks(int ticks) {
        this.getPlayer().setNoDamageTicks(ticks);
    }

    public Player getKiller() {
        return this.getPlayer().getKiller();
    }

    public boolean addPotionEffect(PotionEffect effect) {
        return this.getPlayer().addPotionEffect(effect);
    }

    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return this.getPlayer().addPotionEffect(effect, force);
    }

    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return this.getPlayer().addPotionEffects(effects);
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        return this.getPlayer().hasPotionEffect(type);
    }

    public void removePotionEffect(PotionEffectType type) {
        this.getPlayer().removePotionEffect(type);
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return this.getPlayer().getActivePotionEffects();
    }

    public boolean hasLineOfSight(Entity other) {
        return this.getPlayer().hasLineOfSight(other);
    }

    public boolean getRemoveWhenFarAway() {
        return this.getPlayer().getRemoveWhenFarAway();
    }

    public void setRemoveWhenFarAway(boolean remove) {
        this.getPlayer().setRemoveWhenFarAway(remove);
    }

    public EntityEquipment getEquipment() {
        return this.getPlayer().getEquipment();
    }

    public void setCanPickupItems(boolean pickup) {
        this.getPlayer().setCanPickupItems(pickup);
    }

    public boolean getCanPickupItems() {
        return this.getPlayer().getCanPickupItems();
    }

    public boolean isLeashed() {
        return this.getPlayer().isLeashed();
    }

    public Entity getLeashHolder() throws IllegalStateException {
        return this.getPlayer().getLeashHolder();
    }

    public boolean setLeashHolder(Entity holder) {
        return this.getPlayer().setLeashHolder(holder);
    }

    public Location getLocation() {
        return this.getPlayer().getLocation();
    }

    public Location getLocation(Location loc) {
        return this.getPlayer().getLocation(loc);
    }

    public void setVelocity(Vector velocity) {
        this.getPlayer().setVelocity(velocity);
    }

    public Vector getVelocity() {
        return this.getPlayer().getVelocity();
    }

    public World getWorld() {
        return this.getPlayer().getWorld();
    }

    public boolean teleport(Location location) {
        return this.getPlayer().teleport(location);
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        return this.getPlayer().teleport(location, cause);
    }

    public boolean teleport(Entity destination) {
        return this.getPlayer().teleport(destination);
    }

    public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
        return this.getPlayer().teleport(destination, cause);
    }

    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return this.getPlayer().getNearbyEntities(x, y, z);
    }

    public int getEntityId() {
        return this.getPlayer().getEntityId();
    }

    public int getFireTicks() {
        return this.getPlayer().getFireTicks();
    }

    public int getMaxFireTicks() {
        return this.getPlayer().getMaxFireTicks();
    }

    public void setFireTicks(int ticks) {
        this.getPlayer().setFireTicks(ticks);
    }

    public void remove() {
        this.getPlayer().remove();
    }

    public boolean isDead() {
        return this.getPlayer().isDead();
    }

    public boolean isValid() {
        return this.getPlayer().isValid();
    }

    public Server getServer() {
        return this.getPlayer().getServer();
    }

    public Entity getPassenger() {
        return this.getPlayer().getPassenger();
    }

    public boolean setPassenger(Entity passenger) {
        return this.getPlayer().setPassenger(passenger);
    }

    public boolean isEmpty() {
        return this.getPlayer().isEmpty();
    }

    public boolean eject() {
        return this.getPlayer().eject();
    }

    public float getFallDistance() {
        return this.getPlayer().getFallDistance();
    }

    public void setFallDistance(float distance) {
        this.getPlayer().setFallDistance(distance);
    }

    public void setLastDamageCause(EntityDamageEvent event) {
        this.getPlayer().setLastDamageCause(event);
    }

    public EntityDamageEvent getLastDamageCause() {
        return this.getPlayer().getLastDamageCause();
    }

    public int getTicksLived() {
        return this.getPlayer().getTicksLived();
    }

    public void setTicksLived(int value) {
        this.getPlayer().setTicksLived(value);
    }

    public void playEffect(EntityEffect type) {
        this.getPlayer().playEffect(type);
    }

    public EntityType getType() {
        return this.getPlayer().getType();
    }

    public boolean isInsideVehicle() {
        return this.getPlayer().isInsideVehicle();
    }

    public boolean leaveVehicle() {
        return this.getPlayer().leaveVehicle();
    }

    public Entity getVehicle() {
        return this.getPlayer().getVehicle();
    }

    public void setCustomName(String name) {
        this.getPlayer().setCustomName(name);
    }

    public String getCustomName() {
        return this.getPlayer().getCustomName();
    }

    public void setCustomNameVisible(boolean flag) {
        this.getPlayer().setCustomNameVisible(flag);
    }

    public boolean isCustomNameVisible() {
        return this.getPlayer().isCustomNameVisible();
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.getPlayer().setMetadata(metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.getPlayer().getMetadata(metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return this.getPlayer().hasMetadata(metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.getPlayer().removeMetadata(metadataKey, owningPlugin);
    }

    public void sendMessage(String[] messages) {
        this.getPlayer().sendMessage(messages);
    }

    public boolean isPermissionSet(String name) {
        return this.getPlayer().isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return this.getPlayer().isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return this.getPlayer().hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return this.getPlayer().hasPermission(perm);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return this.getPlayer().addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.getPlayer().addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return this.getPlayer().addAttachment(plugin, name, value, ticks);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return this.getPlayer().addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {
        this.getPlayer().removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        this.getPlayer().recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.getPlayer().getEffectivePermissions();
    }

    public boolean isOp() {
        return this.getPlayer().isOp();
    }

    public void setOp(boolean value) {
        this.getPlayer().setOp(value);
    }

    public void damage(double amount) {
        this.getPlayer().damage(amount);
    }

    @Deprecated
    public void _INVALID_damage(int amount) {
        this.getPlayer()._INVALID_damage(amount);
    }

    public void damage(double amount, Entity source) {
        this.getPlayer().damage(amount, source);
    }

    @Deprecated
    public void _INVALID_damage(int amount, Entity source) {
        this.getPlayer()._INVALID_damage(amount, source);
    }

    public double getHealth() {
        return this.getPlayer().getHealth();
    }

    @Deprecated
    public int _INVALID_getHealth() {
        return this.getPlayer()._INVALID_getHealth();
    }

    public void setHealth(double health) {
        this.getPlayer().setHealth(health);
    }

    public void _INVALID_setHealth(int health) {
        this.getPlayer()._INVALID_setHealth(health);
    }

    public double getMaxHealth() {
        return this.getPlayer().getMaxHealth();
    }

    @Deprecated
    public int _INVALID_getMaxHealth() {
        return this.getPlayer()._INVALID_getMaxHealth();
    }

    public void setMaxHealth(double health) {
        this.getPlayer().setMaxHealth(health);
    }

    @Deprecated
    public void _INVALID_setMaxHealth(int health) {
        this.getPlayer()._INVALID_setMaxHealth(health);
    }

    public void resetMaxHealth() {
        this.getPlayer().resetMaxHealth();
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return (T)this.getPlayer().launchProjectile(projectile);
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return (T)this.getPlayer().launchProjectile(projectile, velocity);
    }

    public boolean isConversing() {
        return this.getPlayer().isConversing();
    }

    public void acceptConversationInput(String input) {
        this.getPlayer().acceptConversationInput(input);
    }

    public boolean beginConversation(Conversation conversation) {
        return this.getPlayer().beginConversation(conversation);
    }

    public void abandonConversation(Conversation conversation) {
        this.getPlayer().abandonConversation(conversation);
    }

    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        this.getPlayer().abandonConversation(conversation, details);
    }

    public boolean isBanned() {
        return this.getPlayer().isBanned();
    }

    @Deprecated
    public void setBanned(boolean banned) {
        this.getPlayer().setBanned(banned);
    }

    public boolean isWhitelisted() {
        return this.getPlayer().isWhitelisted();
    }

    public void setWhitelisted(boolean value) {
        this.getPlayer().setWhitelisted(value);
    }

    public long getFirstPlayed() {
        return this.getPlayer().getFirstPlayed();
    }

    public long getLastPlayed() {
        return this.getPlayer().getLastPlayed();
    }

    public boolean hasPlayedBefore() {
        return this.getPlayer().hasPlayedBefore();
    }

    public Map<String, Object> serialize() {
        return this.getPlayer().serialize();
    }

    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        this.getPlayer().sendPluginMessage(source, channel, message);
    }

    public Set<String> getListeningPluginChannels() {
        return this.getPlayer().getListeningPluginChannels();
    }

    public void playSound(Location lctn, Sound sound, SoundCategory sc, float f, float f1) {
        this.getPlayer().playSound(lctn, sound, sc, f, f1);
    }

    public void playSound(Location lctn, String string, SoundCategory sc, float f, float f1) {
        this.getPlayer().playSound(lctn, string, sc, f, f1);
    }

    public void stopSound(Sound sound) {
        this.getPlayer().stopSound(sound);
    }

    public void stopSound(String string) {
        this.getPlayer().stopSound(string);
    }

    public void stopSound(Sound sound, SoundCategory sc) {
        this.getPlayer().stopSound(sound, sc);
    }

    public void stopSound(String string, SoundCategory sc) {
        this.getPlayer().stopSound(string, sc);
    }

    public void sendTitle(String string, String string1, int i, int i1, int i2) {
        this.getPlayer().sendTitle(string, string1, i, i1, i2);
    }

    public void spawnParticle(Particle prtcl, Location lctn, int i) {
        this.getPlayer().spawnParticle(prtcl, lctn, i);
    }

    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i);
    }

    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, T t) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, t);
    }

    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, T t) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, t);
    }

    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2);
    }

    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5);
    }

    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, T t) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, t);
    }

    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, T t) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, t);
    }

    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, d3);
    }

    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, d6);
    }

    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3, T t) {
        this.getPlayer().spawnParticle(prtcl, lctn, i, d, d1, d2, d3, t);
    }

    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6, T t) {
        this.getPlayer().spawnParticle(prtcl, d, d1, d2, i, d3, d4, d5, d6, t);
    }

    public MainHand getMainHand() {
        return this.getPlayer().getMainHand();
    }

    public InventoryView openMerchant(Villager vlgr, boolean bln) {
        return this.getPlayer().openMerchant(vlgr, bln);
    }

    public InventoryView openMerchant(Merchant mrchnt, boolean bln) {
        return this.getPlayer().openMerchant(mrchnt, bln);
    }

    public boolean isHandRaised() {
        return this.getPlayer().isHandRaised();
    }

    public PotionEffect getPotionEffect(PotionEffectType pet) {
        return this.getPlayer().getPotionEffect(pet);
    }

    public boolean isGliding() {
        return this.getPlayer().isGliding();
    }

    public void setGliding(boolean bln) {
        this.getPlayer().setGliding(bln);
    }

    public void setAI(boolean bln) {
        this.getPlayer().setAI(bln);
    }

    public boolean hasAI() {
        return this.getPlayer().hasAI();
    }

    public void setCollidable(boolean bln) {
        this.getPlayer().setCollidable(bln);
    }

    public boolean isCollidable() {
        return this.getPlayer().isCollidable();
    }

    public AttributeInstance getAttribute(Attribute atrbt) {
        return this.getPlayer().getAttribute(atrbt);
    }

    public List<Entity> getPassengers() {
        return this.getPlayer().getPassengers();
    }

    public boolean addPassenger(Entity entity) {
        return this.getPlayer().addPassenger(entity);
    }

    public boolean removePassenger(Entity entity) {
        return this.getPlayer().removePassenger(entity);
    }

    public void setGlowing(boolean bln) {
        this.getPlayer().setGlowing(bln);
    }

    public boolean isGlowing() {
        return this.getPlayer().isGlowing();
    }

    public void setInvulnerable(boolean bln) {
        this.getPlayer().setInvulnerable(bln);
    }

    public boolean isInvulnerable() {
        return this.getPlayer().isInvulnerable();
    }

    public boolean isSilent() {
        return this.getPlayer().isSilent();
    }

    public void setSilent(boolean bln) {
        this.getPlayer().setSilent(bln);
    }

    public boolean hasGravity() {
        return this.getPlayer().hasGravity();
    }

    public void setGravity(boolean bln) {
        this.getPlayer().setGravity(bln);
    }

    public int getPortalCooldown() {
        return this.getPlayer().getPortalCooldown();
    }

    public void setPortalCooldown(int i) {
        this.getPlayer().setPortalCooldown(i);
    }

    public Set<String> getScoreboardTags() {
        return this.getPlayer().getScoreboardTags();
    }

    public boolean addScoreboardTag(String string) {
        return this.getPlayer().addScoreboardTag(string);
    }

    public boolean removeScoreboardTag(String string) {
        return this.getPlayer().removeScoreboardTag(string);
    }
}
