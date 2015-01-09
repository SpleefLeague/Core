/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.listeners;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.menus.MenuRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Jonas
 */
public class ItemMenuListener implements Listener {
    
    private static Listener instance;
    
    public static void init() {
        if(instance == null) {
            instance = new ItemMenuListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private ItemMenuListener() {
        
    }
    
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack is = event.getItem();
            if(is.equals(MenuRepository.getSLMenuItem())) {
                MenuRepository.showMenu(SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer()));
            }
        }
    }
    
    @EventHandler
    public void onInventoryAction(InventoryClickEvent event) {
        if(event.getCurrentItem() != null && event.getCurrentItem().equals(MenuRepository.getSLMenuItem())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(event.getItemDrop().equals(MenuRepository.getSLMenuItem())) {
            event.setCancelled(true);
        }
    }
}
