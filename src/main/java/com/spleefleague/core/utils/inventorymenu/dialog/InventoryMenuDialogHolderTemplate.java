/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogHolderTemplate<B> extends AbstractInventoryMenuTemplate<InventoryMenuDialogHolder<B>> {
    
    @Override
    public InventoryMenuDialogHolder construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();
        InventoryMenuDialogHolder menu = new InventoryMenuDialogHolder(is, getTitle(slp), components, staticComponents, super.getAccessController(), super.getVisibilityController(), slp, flags);
        return menu;
    }
    
}
