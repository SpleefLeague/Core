/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplate;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogItemTemplate<B> extends InventoryMenuComponentTemplate<InventoryMenuDialogItem<B>>{

    @Override
    public InventoryMenuDialogItem<B> construct(AbstractInventoryMenu parent, SLPlayer slp) {
        ItemStackWrapper isw = constructDisplayItem();
        return new InventoryMenuDialogItem(parent, isw, getVisibilityController(), getAccessController(), getOverwritePageBehavior());
    }
}
