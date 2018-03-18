/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponent;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogComponent extends InventoryMenuComponent{

    public InventoryMenuDialogComponent(ItemStackWrapper displayItem, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, boolean overwritePageBehavior) {
        super(displayItem, visibilityController, accessController, overwritePageBehavior);
    }
}
