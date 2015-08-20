package com.spleefleague.core.utils.inventorymenu;

import org.bukkit.entity.Player;

public class InventoryMenuClickEvent {

    private final InventoryMenuItem item;
    private final Player player;

    public InventoryMenuClickEvent(InventoryMenuItem item, Player player) {
        this.item = item;
        this.player = player;
    }

    public InventoryMenuItem getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }
}
