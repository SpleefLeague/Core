package com.spleefleague.core.utils.inventorymenu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryMenuItem extends InventoryMenuComponent {

    private final InventoryMenuClickListener onClick;

    public InventoryMenuItem(ItemStack displayItem, InventoryMenuClickListener onClick) {
        super(displayItem);
        this.onClick = onClick;
    }

    @Override
    void selected(Player p) {
        if (onClick != null) {
            onClick.onClick(new InventoryMenuClickEvent(this, p));
        }
    }
}
