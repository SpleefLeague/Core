package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import org.bukkit.event.inventory.ClickType;

public class InventoryMenuItem extends InventoryMenuComponent {

    private final InventoryMenuClickListener onClick;

    public InventoryMenuItem(ItemStackWrapper displayItem, InventoryMenuClickListener onClick, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, boolean overwritePageBehavior) {
        super(displayItem, visibilityController, accessController, overwritePageBehavior);
        this.onClick = onClick;
    }

    @Override
    protected void selected(ClickType clickType) {
        if (onClick != null) {
            onClick.onClick(new InventoryMenuClickEvent(this, clickType, this.getParent().getOwner()));
        }
    }
}
