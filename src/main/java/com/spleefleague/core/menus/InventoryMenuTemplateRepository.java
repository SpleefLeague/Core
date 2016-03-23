/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.menus;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Jonas
 */
public class InventoryMenuTemplateRepository {

    private static final Set<InventoryMenuTemplate> menus = new HashSet<>();

    public static void initTemplates() {
        SLMenu.init();
    }

    public static void addMenu(InventoryMenuTemplate menu) {
        menus.add(menu);
    }

    public static boolean isMenuItem(ItemStack is, SLPlayer slp) {
        return menus.stream().anyMatch((template) -> (template.getDisplayItemStack(slp).equals(is)));
    }

    public static void openMenu(ItemStack is, SLPlayer slp) {
        Optional<InventoryMenuTemplate> oimt = menus.stream().filter((template) -> (template.getDisplayItemStack(slp).equals(is))).findFirst();
        if (oimt.isPresent()) {
            oimt.get().construct(slp).open();
        }
    }
}
