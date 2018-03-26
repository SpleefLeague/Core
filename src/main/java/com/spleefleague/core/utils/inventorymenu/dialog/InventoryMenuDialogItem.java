/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.function.Function;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogItem<B> extends InventoryMenuDialogComponent<B> {
    
    public InventoryMenuDialogItem(
            AbstractInventoryMenu parent,
            ItemStackWrapper displayItem, 
            Function<SLPlayer, Boolean> visibilityController, 
            Function<SLPlayer, Boolean> accessController, 
            int flags) {
        super(parent, displayItem, visibilityController, accessController, flags);
    }
    
    @Override
    protected void selected(ClickType clickType, B builder) {
        
    }
}
