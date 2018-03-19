package com.spleefleague.core.utils.inventorymenu.dialog;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class InventoryMenuDialogClickEvent<B> {

    private final B builder;
    private final InventoryMenuDialogItem<B> item;
    private final ClickType clickType;
    private final Player player;
    
    public InventoryMenuDialogClickEvent(InventoryMenuDialogItem<B> item, ClickType clickType, Player player, B builder) {
        this.item = item;
        this.clickType = clickType;
        this.player = player;
        this.builder = builder;
    }
    
    public InventoryMenuDialogItem<B> getItem() {
        return item;
    }
    
    public ClickType getClickType() {
        return clickType;
    }
    
    public Player getPlayer() {
        return player;
    }
    public B getBuilder() {
        return builder;
    }
}
