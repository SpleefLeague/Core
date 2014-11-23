/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.listeners;

import net.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 *
 * @author Jonas
 */
public class EnvironmentListener implements Listener{
    
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new EnvironmentListener(), SpleefLeague.getInstance());
    }
    
    private EnvironmentListener() {
        
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
        if(!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
        }
    }
    
    
}
