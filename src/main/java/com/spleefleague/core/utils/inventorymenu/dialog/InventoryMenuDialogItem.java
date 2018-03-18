/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogItem<B> extends InventoryMenuDialogComponent {
    
    private final InventoryMenuDialogClickListener<B> clickListener;
    private final Supplier<InventoryMenuDialogTemplate<B>> next;
    
    public InventoryMenuDialogItem(
            ItemStackWrapper displayItem, 
            Function<SLPlayer, Boolean> visibilityController, 
            Function<SLPlayer, Boolean> accessController, 
            boolean overwritePageBehavior, 
            InventoryMenuDialogClickListener<B> clickListener, 
            Supplier<InventoryMenuDialogTemplate<B>> next) {
        super(displayItem, visibilityController, accessController, overwritePageBehavior);
        this.clickListener = clickListener;
        this.next = next;
    }
}
