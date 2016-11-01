package com.spleefleague.core.utils.inventorymenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class InventoryMenuClickEvent {

    private final InventoryMenuItem item;
    private final ClickType clickType;
    private final Player player;
    
    public InventoryMenuClickEvent(InventoryMenuItem item, ClickType clickType, Player player) {
        this.item = item;
        this.clickType = clickType;
        this.player = player;
    }
    
    public InventoryMenuItem getItem() {
        return item;
    }
    
    public ClickType getClickType() {
        return clickType;
    }
    
    public Player getPlayer() {
        return player;
    }
    
}
