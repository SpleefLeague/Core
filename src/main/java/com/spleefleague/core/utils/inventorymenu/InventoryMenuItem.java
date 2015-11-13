package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.utils.function.Dynamic;
import org.bukkit.entity.Player;

public class InventoryMenuItem extends InventoryMenuComponent {

    private final InventoryMenuClickListener onClick;

    public InventoryMenuItem(ItemStackWrapper displayItem, InventoryMenuClickListener onClick, Dynamic<Boolean> visibilityController) {
        super(displayItem, visibilityController);
        this.onClick = onClick;
    }

    @Override
    protected void selected() {
        if (onClick != null) {
            onClick.onClick(new InventoryMenuClickEvent(this, this.getParent().getOwner().getPlayer()));
        }
    }
}
