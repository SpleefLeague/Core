package com.spleefleague.core.utils.inventorymenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponent {

    private final ItemStack displayItem;

    private InventoryMenu parent;

    public InventoryMenuComponent(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public InventoryMenu getParent() {
        return parent;
    }

    void setParent(InventoryMenu parent) {
        this.parent = parent;
    }

    public InventoryMenuComponent getRoot() {
        if (parent == null) {
            return this;
        }
        else {
            return parent.getRoot();
        }

    }

    abstract void selected(Player player);

}
