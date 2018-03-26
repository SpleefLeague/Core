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
    public InventoryMenuItem construct(AbstractInventoryMenu parent, SLPlayer slp) {
        ItemStackWrapper isw = constructDisplayItem();
        return new InventoryMenuItem(parent, isw, onClick, getVisibilityController(), getAccessController(), getComponentFlags());
    }
}
