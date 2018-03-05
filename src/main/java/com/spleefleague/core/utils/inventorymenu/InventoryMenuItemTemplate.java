package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;

public class InventoryMenuItemTemplate extends InventoryMenuComponentTemplate<InventoryMenuItem> {

    private InventoryMenuClickListener onClick;

    protected InventoryMenuItemTemplate() {

    }

    public InventoryMenuClickListener getOnClick() {
        return onClick;
    }

    public void setOnClick(InventoryMenuClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public InventoryMenuItem construct(SLPlayer slp) {
        ItemStackWrapper isw = constructDisplayItem();
        return new InventoryMenuItem(isw, onClick, getVisibilityController(), getAccessController(), getOverwritePageBehavior());
    }
}
