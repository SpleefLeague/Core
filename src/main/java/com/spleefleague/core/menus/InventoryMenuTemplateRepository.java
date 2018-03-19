/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.menus;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.SLPlayer;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.dialogItem;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.dialogMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuItemTemplateBuilder;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialogHolderTemplateBuilder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
    
    public static InventoryMenuDialogHolderTemplateBuilder<Boolean> confirmDialog(InventoryMenuItemTemplateBuilder item) {
        InventoryMenuDialogHolderTemplateBuilder<Boolean> holder = dialogMenu(Boolean.class)
                .title("Confirm?");
        //Confirm & Decline
        holder.component(2, dialogItem(Boolean.class)
                .displayItem(new ItemStack(Material.DIAMOND_AXE, (short)10))
                .onClick((e) -> false)
        );
        holder.component(6, dialogItem(Boolean.class)
                .displayItem(new ItemStack(Material.DIAMOND_AXE, (short)11))
                .onClick((e) -> true)
        );
        //What are we confirming/declining?
        holder.component(4, item);
        return holder;
    }
    
    public static InventoryMenuDialogHolderTemplateBuilder<SLPlayer> playerSelector() {
        return playerSelector((s) -> true);
    }
    
    public static InventoryMenuDialogHolderTemplateBuilder<SLPlayer> playerSelector(Predicate<SLPlayer> include) {
        InventoryMenuDialogHolderTemplateBuilder<SLPlayer> holder = dialogMenu(SLPlayer.class)
                .title("Select a player");
        SpleefLeague.getInstance().getPlayerManager()
                .getAll()
                .stream()
                .filter(include)
                .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                .forEach(p -> {
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM);
                    skull.setDurability((short) 3);
                    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                    skullMeta.setOwner(p.getName());
                    skull.setItemMeta(skullMeta);
                    holder.component(dialogItem(SLPlayer.class)
                            .displayItem(skull)
                            .displayName(p.getName())
                            .onClick(e -> {
                                return p;
                            })
                    );
                });
        return holder;
    }
}
