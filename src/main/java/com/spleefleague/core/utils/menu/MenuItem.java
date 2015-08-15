/*
 * Copyright (c) 2013 All Right Reserved, http://www.multicu.be/
 */
package com.spleefleague.core.utils.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;

/**
 *
 * @author Codepanda
 */
public class MenuItem {

    private final String name;
    private List<String> lore;
    private final Material material;
    private MenuClickListener listener;
    private int amount;
    private short durability;
    private Potion potion;

    public MenuItem(String name, Material material) {
        this.name = name;
        this.material = material;
        this.amount = 1;
        this.durability = -1;
    }

    public MenuItem(String name, ItemStack itemStack) {
        this.name = name;
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.durability = itemStack.getDurability();
        this.lore = itemStack.getItemMeta().getLore();
    }

    public MenuItem setAmount(int amount) {
        this.amount = amount;

        return this;
    }

    public MenuItem setListener(MenuClickListener listener) {
        this.listener = listener;

        return this;
    }

    public MenuItem setLore(String... lore) {
        if (lore == null) {
            this.lore = null;
            return this;
        }
        
        this.lore = new ArrayList<>(Arrays.asList(lore));

        return this;
    }
    
    public String[] getLore() {
        return this.lore.toArray(new String[0]);
    }

    public ItemStack buildItem(Player p) {
        ItemStack is = new ItemStack(material == Material.SKULL ? Material.SKULL_ITEM : material, amount);

        if (potion != null) {
            is = potion.toItemStack(amount);
        }
        
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.RESET + name);

        if (lore != null) {
            im.setLore(lore);
        }

        if (durability > -1) {
            is.setDurability(durability);
        }

        is.setItemMeta(im);
        
        return is;
    }

    public void onClick(InventoryClickEvent event) {
        if (listener != null) {
            listener.onClick(event, this);
        }
    }

    public MenuItem setData(short data) {
        this.durability = data;

        return this;
    }
    
    public MenuItem setPotion(Potion potion) {
        this.potion = potion;
        
        return this;
    }
}
