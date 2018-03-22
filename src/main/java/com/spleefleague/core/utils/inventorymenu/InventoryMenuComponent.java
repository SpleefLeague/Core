package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponent extends AbstractInventoryMenuComponent {

    private final ItemStackWrapper displayItem;
    private final boolean overwritePageBehavior;
    private final Function<SLPlayer, Boolean> visibilityController, accessController;

    public InventoryMenuComponent(AbstractInventoryMenu parent, ItemStackWrapper displayItem, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, boolean overwritePageBehavior) {
        super(parent);
        this.displayItem = displayItem;
        this.visibilityController = visibilityController;
        this.accessController = accessController;
        this.overwritePageBehavior = overwritePageBehavior;
    }

    public Function<SLPlayer, Boolean> getVisibilityController() {
        return visibilityController;
    }

    public Function<SLPlayer, Boolean> getAccessController() {
        return accessController;
    }
    
    public boolean getOverwritePageBehavior() {
        return this.overwritePageBehavior;
    }

    public ItemStackWrapper getDisplayItemWrapper() {
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

    public InventoryMenuComponent getRoot() {
        if (this.getParent() == null) {
            return this;
        } else {
            return this.getParent().getRoot();
        }
    }
}
