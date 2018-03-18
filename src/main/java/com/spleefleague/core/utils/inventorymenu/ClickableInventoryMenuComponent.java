/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 */
public abstract class ClickableInventoryMenuComponent extends InventoryMenuComponent {

    public ClickableInventoryMenuComponent(ItemStackWrapper displayItem, Function<SLPlayer, Boolean> visibilityController, Function<SLPlayer, Boolean> accessController, boolean overwritePageBehavior) {
        super(displayItem, visibilityController, accessController, overwritePageBehavior);
    }
    
    protected abstract void selected(ClickType clickType);
}
