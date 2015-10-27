/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.commands.back;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.DBSaveable;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.io.TypeConverter;
import com.spleefleague.core.io.TypeConverter.DateConverter;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.GamePlugin;

/**
 *
 * @author Jonas
 */
public class EnvironmentListener implements Listener{
    
    private static Listener instance;
    
    public static void init() {
        if(instance == null) {
            instance = new EnvironmentListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private EnvironmentListener() {
        
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(SpleefLeague.getInstance().getSpawnLocation());
//        player.getInventory().setItem(0, MenuRepository.getSLMenuItem());
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has joined the server");
        if(!player.hasPlayedBefore()) {
            ChatManager.sendMessage(SpleefLeague.getInstance().getChatPrefix(), ChatColor.BLUE + "Welcome " + ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.BLUE + " to SpleefLeague!", "DEFAULT");
        }
        Bukkit.getScheduler().runTaskLater(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                    slp.getPlayer().setPlayerListName(slp.getRank().getColor() + slp.getName());
                    slp.getPlayer().setDisplayName(slp.getRank().getColor() + slp.getName());
                }
            }
        },20);
        logIPAddress(event.getPlayer());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has left the server");
        GamePlugin.unspectateGlobal(event.getPlayer());
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && ((Player)event.getDamager()).getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(false);
        }
        else {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(event.getCause() == TeleportCause.COMMAND) {      
            ((back)SpleefLeague.getInstance().getBasicCommand("back")).setLastTeleport(event.getPlayer(), event.getFrom());
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getCause() != DamageCause.ENTITY_ATTACK && (event.getEntity() instanceof Player)) {
            if(event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK) {
                event.setCancelled(true);
            }
            else {
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get((Player)event.getEntity());
                if(slp != null) {
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
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);
        if(!(event.isCancelled() || GamePlugin.isIngameGlobal(event.getPlayer()))) event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
    	if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
    		event.setCancelled(true);
    	
    	if(event.getItemInHand().equals(InventoryMenuTemplateRepository.modMenu.getDisplayItemStackFor(event.getPlayer())))
    		event.setCancelled(true);
    	
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(event.getItemDrop().getItemStack().getType() != Material.RED_ROSE && event.getPlayer().getGameMode() != GameMode.CREATIVE);
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
    public void onInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getItem();
                Material clicked = event.getClickedBlock().getType();
                if(clicked == Material.CAULDRON) {
                    event.getPlayer().setItemInHand(null);
                }
                else if(Arrays.asList(/*Material.CHEST, Material.FURNACE, */Material.DROPPER, Material.ITEM_FRAME, Material.REDSTONE_COMPARATOR, Material.DIODE, Material.DISPENSER, Material.ANVIL, Material.TRAP_DOOR, Material.BED, Material.HOPPER, Material.HOPPER_MINECART).contains(clicked)) {
                    event.setCancelled(true);
                }
                else if(item != null && Arrays.asList(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET).contains(item.getType())) {
                    event.setCancelled(true);
                }
            }
            else if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if(event.getClickedBlock().getType() == Material.FIRE) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0];
        if(((List<String>)Settings.getList("blocked_commands")).contains(cmd)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.SOIL && event.getEntity() instanceof Creature) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFrameBrake(HangingBreakByEntityEvent e) {
        e.setCancelled(true);
        if(e.getRemover() instanceof Player) {
            e.setCancelled(((Player)e.getRemover()).getGameMode() != GameMode.CREATIVE);
        }
    }
    
    private void logIPAddress(final Player player) {
        final String ip = player.getAddress().getAddress().toString();
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                EntityBuilder.save(new Connection(player.getUniqueId(), ip), SpleefLeague.getInstance().getPluginDB().getCollection("PlayerConnections"));
            }
        });
    }
    
    public static class Connection extends DBEntity implements DBSaveable {
        
        @DBSave(fieldName = "uuid", typeConverter = TypeConverter.UUIDStringConverter.class)
        private UUID uuid;
        @DBSave(fieldName = "ip")
        private String ip;
        @DBSave(fieldName = "date", typeConverter = DateConverter.class)
        private Date date;
        
        public Connection(UUID uuid, String ip) {
            this.uuid = uuid;
            this.ip = ip;
            this.date = new Date();
        }
    }
}
