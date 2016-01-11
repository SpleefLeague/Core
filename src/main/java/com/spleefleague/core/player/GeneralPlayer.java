/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import java.util.UUID;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBLoadable;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.DBSaveable;
import com.spleefleague.core.io.TypeConverter.UUIDStringConverter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
    
    public GeneralPlayer() {
        
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
        if(cached != null && cached.isOnline()) {
            return cached;
        }
        else {
            return cached = Bukkit.getPlayer(uuid);
        }
    }
    
    @Override
    public boolean isOnline() {
        Player p = getPlayer();
        return p != null && p.isOnline();
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
        for(int i = 0; i < players.length; i++) {
            bplayers[i] = players[i].getPlayer();
        }
        return bplayers;
    }
    
    public static Collection<Player> toBukkitPlayer(Collection<GeneralPlayer> players) {
        Collection<Player> list = new ArrayList<>();
        for(GeneralPlayer gp : players) {
            list.add(gp.getPlayer());
        }
        return list;
    }
    
    public void setDefaults() {
        
    }

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
        cached = null;
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
    @Deprecated
    public void sendBlockChange(Location loc, int material, byte data) {
        getPlayer().sendBlockChange(loc, material, data);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines) throws IllegalArgumentException {
        getPlayer().sendSignChange(loc, lines);
    }

    @Override
    public void sendMap(MapView map) {
        getPlayer().sendMap(map);
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
    
    public void hidePlayer(GeneralPlayer player) {
        getPlayer().hidePlayer(player.getPlayer());
    }

    public void showPlayer(GeneralPlayer player) {
        getPlayer().showPlayer(player.getPlayer());
    }

    public boolean canSee(GeneralPlayer player) {
        return getPlayer().canSee(player.getPlayer());
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
    public Spigot spigot() {
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
    @Deprecated
    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return getPlayer().getLineOfSight(transparent, maxDistance);
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return getPlayer().getLineOfSight(transparent, maxDistance);
    }

    @Override
    @Deprecated
    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        return getPlayer().getTargetBlock(transparent, maxDistance);
    }

    @Override
    public Block getTargetBlock(Set<Material> transparent, int maxDistance) {
        return getPlayer().getTargetBlock(transparent, maxDistance);
    }

    @Override
    @Deprecated
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        return getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> transparent, int maxDistance) {
        return getPlayer().getLastTwoTargetBlocks(transparent, maxDistance);
    }

    @Override
    @Deprecated
    public Egg throwEgg() {
        return getPlayer().throwEgg();
    }

    @Override
    @Deprecated
    public Snowball throwSnowball() {
        return getPlayer().throwSnowball();
    }

    @Override
    @Deprecated
    public Arrow shootArrow() {
        return getPlayer().shootArrow();
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
    @Deprecated
    public int _INVALID_getLastDamage() {
        return getPlayer()._INVALID_getLastDamage();
    }

    @Override
    public void setLastDamage(double damage) {
        getPlayer().setLastDamage(damage);
    }

    @Override
    @Deprecated
    public void _INVALID_setLastDamage(int damage) {
        getPlayer()._INVALID_setLastDamage(damage);
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
    @Deprecated
    public void _INVALID_damage(int amount) {
        getPlayer()._INVALID_damage(amount);
    }

    @Override
    public void damage(double amount, Entity source) {
        getPlayer().damage(amount, source);
    }

    @Override
    @Deprecated
    public void _INVALID_damage(int amount, Entity source) {
        getPlayer()._INVALID_damage(amount, source);
    }

    @Override
    public double getHealth() {
        return getPlayer().getHealth();
    }

    @Override
    @Deprecated
    public int _INVALID_getHealth() {
        return getPlayer()._INVALID_getHealth();
    }

    @Override
    public void setHealth(double health) {
        getPlayer().setHealth(health);
    }

    @Override
    public void _INVALID_setHealth(int health) {
        getPlayer()._INVALID_setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return getPlayer().getMaxHealth();
    }

    @Override
    @Deprecated
    public int _INVALID_getMaxHealth() {
        return getPlayer()._INVALID_getMaxHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        getPlayer().setMaxHealth(health);
    }

    @Override
    @Deprecated
    public void _INVALID_setMaxHealth(int health) {
        getPlayer()._INVALID_setMaxHealth(health);
    }

    @Override
    public void resetMaxHealth() {
        getPlayer().resetMaxHealth();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return getPlayer().launchProjectile(projectile);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return getPlayer().launchProjectile(projectile, velocity);
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
    @Deprecated
    public void setBanned(boolean banned) {
        getPlayer().setBanned(banned);
    }

    @Override
    public boolean isWhitelisted() {
        return getPlayer().isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean value) {
        getPlayer().setWhitelisted(value);
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
}