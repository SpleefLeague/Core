package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.utils.function.Dynamic;

public class InventoryMenuItem extends InventoryMenuComponent {

    private final InventoryMenuClickListener onClick;

    public InventoryMenuItem(ItemStackWrapper displayItem, InventoryMenuClickListener onClick, Dynamic<Boolean> visibilityController, Dynamic<Boolean> accessController) {
        super(displayItem, visibilityController, accessController);
        this.onClick = onClick;
    }

    @Override
    protected void selected() {
        if (onClick != null) {
            onClick.onClick(new InventoryMenuClickEvent(this, this.getParent().getOwner().getPlayer()));
        }
    }
}
