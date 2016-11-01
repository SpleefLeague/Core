package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponent {

    private final ItemStackWrapper displayItem;
    private InventoryMenu parent;
    private final Dynamic<Boolean> visibilityController, accessController;

    public InventoryMenuComponent(ItemStackWrapper displayItem, Dynamic<Boolean> visibilityController, Dynamic<Boolean> accessController) {
        this.displayItem = displayItem;
        this.visibilityController = visibilityController;
        this.accessController = accessController;
    }

    protected ItemStackWrapper getDisplayItemWrapper() {
        return displayItem;
    }

    public ItemStack getDisplayItem(SLPlayer slp) {
        return getDisplayItemWrapper().construct(slp);
    }

    public boolean isVisible(SLPlayer slp) {
        return visibilityController.get(slp);
    }

    public boolean hasAccess(SLPlayer slp) {
        return accessController.get(slp);
    }

    public InventoryMenu getParent() {
        return parent;
    }

    protected void setParent(InventoryMenu parent) {
        this.parent = parent;
    }

    public InventoryMenuComponent getRoot() {
        if (parent == null) {
            return this;
        } else {
            return parent.getRoot();
        }

    }

    abstract void selected(ClickType clickType);
}
