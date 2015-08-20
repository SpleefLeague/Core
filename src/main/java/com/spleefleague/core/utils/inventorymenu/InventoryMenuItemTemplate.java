package com.spleefleague.core.utils.inventorymenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryMenuItemTemplate extends InventoryMenuComponentTemplate<InventoryMenuItem> {

    private InventoryMenuClickListener onClick;

    InventoryMenuItemTemplate() {

    }

    public InventoryMenuClickListener getOnClick() {
        return onClick;
    }

    public void setOnClick(InventoryMenuClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public InventoryMenuItem construct() {
        ItemStack is = constructDisplayItem();

        return new InventoryMenuItem(is, onClick);
    }

    @Override
    public InventoryMenuItem constructFor(Player p) {
        ItemStack is = constructDisplayItem();

        return new InventoryMenuItem(is, onClick);
    }
}
