package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponent {

    private final ItemStackWrapper displayItem;
    private InventoryMenu parent;
    private final boolean overwritePageBehavior;
    private final Function<SLPlayer, Boolean> visibilityController, accessController;

    public InventoryMenuComponent(ItemStackWrapper displayItem, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, boolean overwritePageBehavior) {
        this.displayItem = displayItem;
        this.visibilityController = visibilityController;
        this.accessController = accessController;
        this.overwritePageBehavior = overwritePageBehavior;
    }
    
    protected boolean getOverwritePageBehavior() {
        return this.overwritePageBehavior;
    }

    protected ItemStackWrapper getDisplayItemWrapper() {
        return displayItem;
    }

    public ItemStack getDisplayItem(SLPlayer slp) {
        return getDisplayItemWrapper().construct(slp);
    }

    public boolean isVisible(SLPlayer slp) {
        return visibilityController.apply(slp);
    }

    public boolean hasAccess(SLPlayer slp) {
        return accessController.apply(slp);
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
    
    protected abstract void selected(ClickType clickType);
}
