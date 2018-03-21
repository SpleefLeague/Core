/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuFlag;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogItem<B> extends InventoryMenuDialogComponent<B> {
    
    private final InventoryMenuDialogClickListener<B> clickListener;
    private Supplier<InventoryMenuDialogHolderTemplate<B>> next;
    private InventoryMenuDialogHolder<B> holderInstance;
    
    public InventoryMenuDialogItem(
            ItemStackWrapper displayItem, 
            Function<SLPlayer, Boolean> visibilityController, 
            Function<SLPlayer, Boolean> accessController, 
            boolean overwritePageBehavior, 
            InventoryMenuDialogClickListener<B> clickListener, 
            Supplier<InventoryMenuDialogHolderTemplate<B>> next) {
        super(displayItem, visibilityController, accessController, overwritePageBehavior);
        this.clickListener = clickListener;
        this.next = next;
    }
    
    protected void setHolderInstance(InventoryMenuDialogHolder<B> holderInstance) {
        this.holderInstance = holderInstance;
        Supplier<InventoryMenuDialogHolderTemplate<B>> oldNext = this.next;
        this.next = () -> {
            InventoryMenuDialogHolderTemplate<B> n = oldNext.get();
            if(n == null) {
                n = holderInstance.getNext().get();
            }
            return n;
        };
    }

    @Override
    protected void selected(ClickType clickType, B builder) {
        clickListener.onClick(new InventoryMenuDialogClickEvent<>(this, clickType, this.getParent().getOwner(), builder));
        InventoryMenuDialogHolderTemplate<B> nextDialog = next.get();
        if(nextDialog != null) {
            holderInstance.construct(nextDialog);
            InventoryMenuDialogHolder<B> dialog = holderInstance.construct(nextDialog);
            dialog.open();
        }
        else {
            holderInstance.getDialogRoot().completed();
            AbstractInventoryMenu aim = holderInstance.getDialogRoot().getParent();
            if(aim != null && !aim.isSet(InventoryMenuFlag.EXIT_ON_COMPLETE_DIALOG)) {
                aim.update();
                aim.open();
            }
            else {
                this.getParent().getOwner().closeInventory();
            }
        }
    }
}
