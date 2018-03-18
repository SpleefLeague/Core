package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.utils.inventorymenu.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class InventoryMenuDialogClickEvent<B> extends InventoryMenuClickEvent {

    private final B builder;
    
    public InventoryMenuDialogClickEvent(InventoryMenuItem item, ClickType clickType, Player player, B builder) {
        super(item, clickType, player);
        this.builder = builder;
    }

    public B getBuilder() {
        return builder;
    }
}
