/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public abstract class SelectableInventoryMenuComponent extends InventoryMenuComponent implements Selectable {

    public SelectableInventoryMenuComponent(AbstractInventoryMenu parent, ItemStackWrapper displayItem, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, boolean overwritePageBehavior) {
        super(parent, displayItem, visibilityController, accessController, overwritePageBehavior);
    }
}
