/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.listeners;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.menus.MenuRepository;
import net.spleefleague.core.player.PlayerState;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

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
        player.teleport(SpleefLeague.DEFAULT_WORLD.getSpawnLocation());
//        player.getInventory().setItem(0, MenuRepository.getSLMenuItem());
        event.setJoinMessage(null);
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
        if(!(event.isCancelled() || GamePlugin.isIngameAll(event.getPlayer()))) event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
    
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }
}
