/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.listeners;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.commands.back;
import net.spleefleague.core.plugin.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

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
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(SpleefLeague.getInstance().getSpawnLocation());
//        player.getInventory().setItem(0, MenuRepository.getSLMenuItem());
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has joined the server");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has left the server");
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
        if((event.getEntity() instanceof Player)) {
            if(event.getCause() == DamageCause.FALL) {
                event.setCancelled(true);
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
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
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
        if(event.getItem() != null && event.getItem().getType() == Material.WATER_BUCKET || event.getItem().getType() == Material.LAVA_BUCKET || event.getItem().getType() == Material.BUCKET) {
            event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
        }
    }
    
    @EventHandler
    public void onFrameBrake(HangingBreakEvent e) {
        e.setCancelled(true);
        if(e.getEntity() instanceof Player) {
            e.setCancelled(((Player)e.getEntity()).getGameMode() != GameMode.CREATIVE);
        }
    }
}