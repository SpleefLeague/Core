package com.spleefleague.core.utils.inventorymenu;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@Data
public class InventoryMenuClickEvent {

    private final InventoryMenuItem item;
    private final ClickType clickType;
    private final Player player;
    
}
