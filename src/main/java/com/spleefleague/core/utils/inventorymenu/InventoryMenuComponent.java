package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponent {

    private final ItemStackWrapper displayItem;
    private InventoryMenu parent;
    private Dynamic<Boolean> visibilityController;
    
    public InventoryMenuComponent(ItemStackWrapper displayItem, Dynamic<Boolean> visibilityController) {
        this.displayItem = displayItem;
        this.visibilityController = visibilityController;
    }

    protected ItemStackWrapper getDisplayItemWrapper() {
        return displayItem;
    }
    
    public ItemStack getDisplayItem(SLPlayer slp) {
        return getDisplayItemWrapper().construct(slp);
    }
    
    public ItemStack getDisplayItem() {
        return getDisplayItem(null);
    }
    
    public boolean isVisible(SLPlayer slp) {
        return visibilityController.get(slp);
    }
    
    public boolean isVisible() {
        return true;
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
        }
        else {
            return parent.getRoot();
        }

    }

    abstract void selected();
}
