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
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import static com.spleefleague.core.menus.InventoryMenuTemplateRepository.isMenuItem;
import static com.spleefleague.core.menus.InventoryMenuTemplateRepository.openMenu;
import com.spleefleague.core.menus.SLMenu;
import com.spleefleague.core.player.GeneralPlayer;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuFlag;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

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
            if (slp.getInventory().getItem(0) == null || slp.getInventory().getItem(0).getType() == Material.AIR) {
                slp.getInventory().setItem(0, SLMenu.getInstance().getDisplayItemStack(slp));
            } else if (!isMenuItem(slp.getInventory().getItem(0), slp)) {
                slp.sendMessage(Theme.ERROR + "You did not recieve the SLMenu because your inventory's first slot was occupied. Remove the item and reconnect to receive the menu.");
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack is = event.getItem();
            if (is != null) {
                SLPlayer slp = getSLPlayer(event.getPlayer());
                if (isMenuItem(is, slp)) {
                    openMenu(is, slp);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isMenuItem(event.getItemInHand(), getSLPlayer(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isMenuItem(event.getItemDrop().getItemStack(), getSLPlayer(event.getPlayer()))) {
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

                } else {
                    int index = event.getRawSlot();
                    if (index < inventory.getSize()) {
                        menu.selectItem(index, event.getClick());
                    } else {
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
            if (event.getCurrentItem() != null && isMenuItem(event.getCurrentItem(), getSLPlayer(p))) {
                event.setCancelled(true);
            }
        }

    }

    private void exitMenuIfClickOutSide(InventoryMenu menu, Player player) {
        if (menu.isSet(InventoryMenuFlag.EXIT_ON_CLICK_OUTSIDE)) {
            menu.close(player);
        }
    }

    private SLPlayer getSLPlayer(Player player) {
        return SpleefLeague.getInstance().getPlayerManager().get(player);
    }
}
