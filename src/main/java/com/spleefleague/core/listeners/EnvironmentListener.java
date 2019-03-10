/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.comphenix.protocol.PacketType;
import com.mongodb.client.model.Projections;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.io.*;
import com.spleefleague.core.player.GeneralPlayer;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.spawn.SpawnManager.SpawnLocation;
import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;
import com.spleefleague.entitybuilder.EntityBuilder;
import com.spleefleague.entitybuilder.TypeConverter;
import com.spleefleague.entitybuilder.TypeConverter.DateConverter;
import com.spleefleague.virtualworld.event.FakeBlockBreakEvent;
import com.spleefleague.virtualworld.event.FakeBlockPlaceEvent;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.entity.EntityType;
import org.bukkit.event.server.TabCompleteEvent;

/**
 *
 * @author Jonas
 */
public class EnvironmentListener implements Listener {

    private static final Set<Material> NO_INTERACT = new HashSet<>(
            Arrays.asList(
                    Material.DROPPER, 
                    Material.ITEM_FRAME, 
                    Material.COMPARATOR, 
                    Material.REPEATER, 
                    Material.DISPENSER, 
                    Material.ANVIL, 
                    Material.OAK_TRAPDOOR, 
                    Material.RED_BED, 
                    Material.HOPPER, 
                    Material.HOPPER_MINECART,
                    Material.DAYLIGHT_DETECTOR
            )    
    );
    
    private static Listener instance;

    public static void init() {
        if (instance == null) {
            instance = new EnvironmentListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private EnvironmentListener() {

    }
    
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if(event.getBuffer().length() > 0 && event.getBuffer().charAt(0) != '/') {
            event.setCompletions(Bukkit
                    .getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(event.getBuffer().toLowerCase()))
                    .collect(Collectors.toList()));
        }
    }
    
    @EventHandler
    public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if(event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            event.setCancelled(true);
        }
        else if(event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            event.setCancelled(true);
        }
    }
    
    //Block TNT ignition. This is kinda annoying
    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        if(event.getIgnitingBlock() != null && event.getIgnitingBlock().getType() == Material.TNT) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onExplode(ExplosionPrimeEvent event) {
        event.setCancelled(event.getEntityType() == EntityType.PRIMED_TNT);
    }
    
    @EventHandler
    public void onTntPhysics(BlockPhysicsEvent event) {
        if(event.getChangedType() == Material.TNT) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityLeash(PlayerLeashEntityEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setCollidable(false);
        SpawnLocation spawnManager = SpleefLeague.getInstance().getSpawnManager().getNext();
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> player.teleport(spawnManager.getLocation()));
        
//        if(!player.hasPlayedBefore()) {
//            event.setJoinMessage(SpleefLeague.getInstance().getChatPrefix() + " " + ChatColor.BLUE + "Welcome " + ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " to SpleefLeague!");
//        }
//        else {
//            event.setJoinMessage(ChatColor.YELLOW + player.getName() + " has joined the server");
//        }
        event.setJoinMessage(null);//During SWC
        logConnection(event.getPlayer(), true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        //event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has left the server");
        event.setQuitMessage(null);
        logConnection(event.getPlayer(), false);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && ((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.COMMAND) {
            //((back) SpleefLeague.getInstance().getBasicCommand("back")).setLastTeleport(event.getPlayer(), event.getFrom());
        }
    }

    private double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            try {
                return (double) Integer.parseInt(input);
            } catch (Exception e2) {
                throw e;
            }
        }
    }

    private ArrayList<Block> getRelative(Block current) {
        ArrayList<Block> list = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            try {
                Block b = current.getRelative(BlockFace.DOWN, i);
                if (b != null) {
                    list.add(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private void doVelocity(Player player, Sign sign) {
        try {
            double x = parseDouble(sign.getLine(1));
            double y = parseDouble(sign.getLine(2));
            double z = parseDouble(sign.getLine(3));

            player.setVelocity(new Vector(x, y, z));
        } catch(NumberFormatException ex) { }
    }

    @EventHandler
    public void onSign(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(player);

        if (slp == null) {
            return;
        }

        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getFrom().getBlockY() ||
                e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {

            Block block = e.getTo().getBlock();
            boolean shouldCancel = false;

            for (Block min : getRelative(block)) {
                if (min.getType() == Material.SPONGE &&
                        slp.getRank().getLadder() < Rank.MODERATOR.getLadder()) {
                    shouldCancel = true;
                    break;
                } else if (min.getType() == Material.SIGN || min.getType() == Material.WALL_SIGN) {
                    Sign sign = (Sign) min.getState();
                    String first = ChatColor.stripColor(sign.getLine(0)).toLowerCase();

                    if (first.equalsIgnoreCase("[min-rank]")) {
                        Rank found = Rank.valueOf(sign.getLine(1));

                        if (found != null && slp.getRank().getLadder() < found.getLadder()) {
                            shouldCancel = true;
                            break;
                        }
                    } else if (first.equalsIgnoreCase("[max-rank]")) {
                        Rank found = Rank.valueOf(sign.getLine(1));

                        if (found != null && slp.getRank().getLadder() > found.getLadder()) {
                            shouldCancel = true;
                            break;
                        }
                    } else if (first.equalsIgnoreCase("[jump]")) {
                        doVelocity(player, sign);
                        continue;
                    } else if (first.equalsIgnoreCase("[teleport]")) {
                        try {

                            double x = parseDouble(sign.getLine(1));
                            double y = parseDouble(sign.getLine(2));
                            double z = parseDouble(sign.getLine(3));

                            player.teleport(new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(),
                                    player.getLocation().getPitch()
                            ), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        } catch (NumberFormatException ex) {
                        }

                        continue;
                    } else if (sign.getLine(0).equalsIgnoreCase("[effect]")) {
                        try {
                            int id = Integer.valueOf(sign.getLine(1));
                            int time = Integer.valueOf(sign.getLine(2));
                            int level = Integer.valueOf(sign.getLine(3));

                            player.addPotionEffect(
                                    new PotionEffect(PotionEffectType.getById(id), time, level, false), true);
                        } catch (NumberFormatException ex) {
                        }

                        continue;
                    }
                }
            }

            if (shouldCancel) {
                Location newLoc = e.getFrom();
                newLoc.setX(newLoc.getBlockX() + 0.5D);
                newLoc.setY(newLoc.getBlockY());
                newLoc.setZ(newLoc.getBlockZ() + 0.5D);

                e.setTo(newLoc);
                player.teleport(newLoc);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != DamageCause.ENTITY_ATTACK && (event.getEntity() instanceof Player)) {
            if (event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) {
                event.setCancelled(true);
            } else {
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get((Player) event.getEntity());
                if (slp != null) {
                    event.setCancelled(slp.getState() != PlayerState.IDLE);
                }
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFakePlace(FakeBlockPlaceEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFakeBreak(FakeBlockBreakEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(event.getItemDrop().getItemStack().getType() != Material.ROSE_RED && event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.DISPENSER) {
            Dispenser disp = (Dispenser) block.getState();
            disp.getInventory().addItem(new ItemStack[]{event.getItem()});
            disp.update();
        }
    }
    
    @EventHandler
    public void onInteractFlowerpot(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getClickedBlock() == null) return;
        Material type = event.getClickedBlock().getType();
        if(type == Material.FLOWER_POT || type.name().startsWith("POTTED_")) {//There isn't really much support for potted plants :(
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getItem();
                Material clicked = event.getClickedBlock().getType();
                if (clicked == Material.CAULDRON) {
                    event.getPlayer().getInventory().setItemInMainHand(null);
                } 
                else if (NO_INTERACT.contains(clicked)) {
                    event.setCancelled(true);
                } 
                else if (item != null && Arrays.asList(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET).contains(item.getType())) {
                    event.setCancelled(true);
                }
            } 
            else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (event.getClickedBlock().getRelative(BlockFace.UP).getType() == Material.FIRE) {
                    event.setCancelled(true);
                }
            }
        }
        if(event.getAction() == Action.PHYSICAL) {
            Material bType = event.getClickedBlock().getType();
            if(bType == Material.FARMLAND || bType == Material.TURTLE_EGG) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGeneralPlayerLoaded(GeneralPlayerLoadedEvent event) {
        GeneralPlayer gp = event.getGeneralPlayer();
        if (gp instanceof SLPlayer) {
            SLPlayer slPlayer = (SLPlayer) gp;
            if(slPlayer.isOnline()) {
                if(!slPlayer.getRank().hasPermission(SpleefLeague.getInstance().getMinimumJoinRank())
                        && !SpleefLeague.getInstance().getExtraJoinRanks().contains(slPlayer.getRank())) {
                    event.getPlayer().kickPlayer(ChatColor.RED + "You don't have permission to join this server!");
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void rankCheck(AsyncPlayerPreLoginEvent e) {
        Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").find(new Document("uuid", e.getUniqueId().toString()))
                .projection(Projections.fields(Projections.include("rank"), Projections.excludeId())).first();
        if (dbo != null) {
            Rank rank = Rank.DEFAULT;
            try {
                rank = Rank.valueOf(dbo.getString("rank"));
            } catch (Exception ignored) {}
            if(rank == null || (!rank.hasPermission(SpleefLeague.getInstance().getMinimumJoinRank())
                    && !SpleefLeague.getInstance().getExtraJoinRanks().contains(rank))) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                e.setKickMessage(ChatColor.RED + "You don't have permission to join this server!");
            }
        }
    }

    @EventHandler
    public void onNether(PlayerMoveEvent e) {
        SLPlayer slPlayer = SpleefLeague.getInstance().getPlayerManager().get(e.getPlayer());
        if(slPlayer == null || slPlayer.getRank().hasPermission(Rank.MODERATOR) || slPlayer.getRank() == Rank.BUILDER) {
            return;
        }
        if(e.getTo().getBlock().getBiome() == Biome.NETHER) {
            if(e.getFrom().getBlock().getBiome() == Biome.NETHER) {
                slPlayer.teleport(SpleefLeague.getInstance().getSpawnManager().getNext().getLocation());
            } else {
                Location balancedFrom = e.getFrom().clone();
                balancedFrom.setPitch(e.getTo().getPitch());
                balancedFrom.setYaw(e.getTo().getYaw());
                slPlayer.teleport(balancedFrom);
            }
            if(Math.abs(System.currentTimeMillis() - slPlayer.getAreaMessageCooldown()) > TimeUnit.SECONDS.toMillis(10)) {
                slPlayer.updateAreaMessageCooldown();
                slPlayer.sendMessage(Theme.ERROR.buildTheme(true) + "You are unable to enter this area!");
            }
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0].toLowerCase();
        if (!Settings.hasKey("blocked_commands")) {
            return;
        }
        Optional<List> optional = Settings.getList("blocked_commands");
        optional.ifPresent((l) -> {
            if(l.contains(cmd)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND && event.getEntity() instanceof Creature) {
            event.setCancelled(true);
        }
        if (event.getBlock().getType() == Material.FIRE) {
            event.setCancelled(true);
            if (event.getEntity() instanceof Player) {
                event.setCancelled(((Player) event.getEntity()).getGameMode() != GameMode.CREATIVE);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(event.getSpawnReason() != SpawnReason.SPAWNER_EGG);
    }

    @EventHandler
    public void onFrameBrake(HangingBreakByEntityEvent e) {
        e.setCancelled(true);
        if (e.getRemover() instanceof Player) {
            e.setCancelled(((Player) e.getRemover()).getGameMode() != GameMode.CREATIVE);
        }
    }

    @EventHandler
    public void onBlockDecay(LeavesDecayEvent evt) {
        evt.setCancelled(true);
    }

    private void logConnection(final Player player, boolean joined) {
        final String ip = player.getAddress().getAddress().getHostAddress();
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () ->
            EntityBuilder.save(new Connection(player.getUniqueId(), ip, joined), SpleefLeague.getInstance().getPluginDB().getCollection("PlayerConnections")));
    }

    public static class Connection extends DBEntity implements DBSaveable {

        @DBSave(fieldName = "uuid", typeConverter = TypeConverter.UUIDStringConverter.class)
        private UUID uuid;
        @DBSave(fieldName = "ip")
        private String ip;
        @DBSave(fieldName = "date", typeConverter = DateConverter.class)
        private Date date;
        @DBSave(fieldName = "type")
        private String type;
        
        public Connection(UUID uuid, String ip, boolean joined) {
            this.uuid = uuid;
            this.date = new Date();
            this.ip = joined ? ip : null;
            this.type = joined ? "JOIN" : "LEAVE";
        }
    }
}
