package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import org.bukkit.event.inventory.ClickType;

public class InventoryMenuItem extends SelectableInventoryMenuComponent {

    private final InventoryMenuClickListener onClick;

    public InventoryMenuItem(AbstractInventoryMenu parent, ItemStackWrapper displayItem, InventoryMenuClickListener onClick, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, int flags) {
        super(parent, displayItem, visibilityController, accessController, flags);
        this.onClick = onClick;
    }

    @Override
    public void selected(ClickType clickType) {
        if (onClick != null) {
            onClick.onClick(new InventoryMenuClickEvent(this, clickType, this.getParent().getOwner()));
        }
    }
}
