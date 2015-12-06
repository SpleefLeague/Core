/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Jonas
 */
public class ItemStackWrapper {
    
    private final Dynamic<ItemStack> displayItem;
    private final Dynamic<String> displayName;
    private final Dynamic<Material> displayIcon;
    private final Dynamic<Integer> displayNumber;
    private final Dynamic<List<String>> displayDescription;
    
    protected ItemStackWrapper(Dynamic<ItemStack> displayItem, Dynamic<Material> displayIcon, Dynamic<String> displayName, Dynamic<Integer> displayNumber, Dynamic<List<String>> displayDescription) {
        this.displayItem = displayItem;
        this.displayIcon = displayIcon;
        this.displayName = displayName;
        this.displayNumber = displayNumber;
        this.displayDescription = displayDescription;
    }
    
    public ItemStack construct(SLPlayer slp) {
        return constructItemStackFromValues(displayItem.get(slp), displayIcon.get(slp), displayName.get(slp), displayNumber.get(slp), displayDescription.get(slp));
    }
    
    private ItemStack constructItemStackFromValues(ItemStack baseStack, Material icon, String name, Integer number, List<String> description) {
        ItemStack is = baseStack.clone();

        if (icon != null) {
            is.setType(icon);
            //is.setData() is not working...
           //Later: is.getData().setData(icon.getData());
        }

        if (number != null) {
            is.setAmount(number);
        }

        ItemMeta im = is.getItemMeta();

        if (name != null) {
            im.setDisplayName(name);
        }
        if (!description.isEmpty()) {
            im.setLore(description);
        }

        is.setItemMeta(im);

        return is;
    }
}
