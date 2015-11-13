/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.player.GeneralPlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenu;

public class InventoryMenuListener implements Listener {

    private static Listener instance;

    public static void init() {
        if (instance == null) {
            instance = new InventoryMenuListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private InventoryMenuListener() {

    }
    
    @EventHandler
    public void onGeneralPlayerLoaded(GeneralPlayerLoadedEvent event) {
        GeneralPlayer gp = event.getGeneralPlayer();
        if (gp instanceof SLPlayer) {
            SLPlayer slp = (SLPlayer) gp;
            if (slp.getRank().hasPermission(Rank.DEVELOPER)) {
                // InventoryMenuTemplateRepository.showModMenu(slp.getPlayer());
                ItemStack is = InventoryMenuTemplateRepository.modMenu.getDisplayItemStack();
                slp.getPlayer().getInventory().setItem(0, is);
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack is = event.getItem();
            if (is != null) {
                if(is.equals(InventoryMenuTemplateRepository.modMenu.getDisplayItemStack(getSLPlayer(event.getPlayer())))) {
                    InventoryMenuTemplateRepository.showModMenu(event.getPlayer());
            
                }
                else if(InventoryMenuTemplateRepository.testMenu != null && is.equals(InventoryMenuTemplateRepository.testMenu.getDisplayItemStack(getSLPlayer(event.getPlayer())))) {
                    InventoryMenuTemplateRepository.testMenu.construct(getSLPlayer(event.getPlayer())).open();
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().equals(InventoryMenuTemplateRepository.modMenu.getDisplayItemStack(getSLPlayer(event.getPlayer())))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof InventoryMenu) {
            InventoryMenu menu = (InventoryMenu) inventory.getHolder();
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {

                    exitMenuIfClickOutSide(menu, player);

                }
                else {
                    int index = event.getRawSlot();
                    if (index < inventory.getSize()) {
                        menu.selectItem(index);
                    }
                    else {
                        exitMenuIfClickOutSide(menu, player);
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryAction(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null && event.getCurrentItem().equals(InventoryMenuTemplateRepository.modMenu.getDisplayItemStack(getSLPlayer(p)))) {
                event.setCancelled(true);
            }
        }

    }

    private void exitMenuIfClickOutSide(InventoryMenu menu, Player player) {
        if (menu.exitOnClickOutside()) {
            menu.close(player);
        }
    }
    
    private SLPlayer getSLPlayer(Player player) {
        return SpleefLeague.getInstance().getPlayerManager().get(player);
    }
}
