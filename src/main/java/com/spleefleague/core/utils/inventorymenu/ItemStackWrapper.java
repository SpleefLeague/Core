/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.List;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Jonas
 */
public class ItemStackWrapper {

    private final Function<SLPlayer, ItemStack> displayItem;
    private final Function<SLPlayer, String> displayName;
    private final Function<SLPlayer, Material> displayIcon;
    private final Function<SLPlayer, Integer> displayNumber;
    private final Function<SLPlayer, List<String>> displayDescription;

    protected ItemStackWrapper(Function<SLPlayer, ItemStack> displayItem, Function<SLPlayer, Material> displayIcon, Function<SLPlayer, String> displayName, Function<SLPlayer, Integer> displayNumber, Function<SLPlayer, List<String>> displayDescription) {
        this.displayItem = displayItem;
        this.displayIcon = displayIcon;
        this.displayName = displayName;
        this.displayNumber = displayNumber;
        this.displayDescription = displayDescription;
    }

    public ItemStack construct(SLPlayer slp) {
        ItemStack baseStack = displayItem.apply(slp);
        return constructItemStackFromValues(baseStack, displayIcon.apply(slp), displayName.apply(slp), displayNumber.apply(slp), displayDescription.apply(slp));
    }

    private ItemStack constructItemStackFromValues(ItemStack baseStack, Material icon, String name, Integer number, List<String> description) {
        if(baseStack == null) {
            baseStack = new ItemStack(Material.AIR, 0);
        }
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
        im.setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
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
