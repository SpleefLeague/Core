/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.SelectableInventoryMenuComponent;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponent;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.function.Function;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 */
public abstract class InventoryMenuDialogComponent<B> extends InventoryMenuComponent{

    public InventoryMenuDialogComponent(AbstractInventoryMenu parent, ItemStackWrapper displayItem, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, int flags) {
        super(parent, displayItem, visibilityController, accessController, flags);
    }

    protected InventoryMenuDialogComponent(SelectableInventoryMenuComponent cimc) {
        super(cimc.getParent(), cimc.getDisplayItemWrapper(), cimc.getVisibilityController(), cimc.getAccessController(), cimc.getComponentFlags());
    }
    
    protected abstract void selected(ClickType clickType, B builder);
}
