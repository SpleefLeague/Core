/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogHolderTemplate<B> extends AbstractInventoryMenuTemplate<InventoryMenuDialogHolder<B>, InventoryMenuDialogComponent<B>> {
    
    private Function<SLPlayer, Optional<InventoryMenuDialogHolderTemplateBuilder<B>>> next;
    
    public InventoryMenuDialogHolderTemplate() {
        this.next = (x) -> Optional.empty();
    }
    
    public void setNext(Function<SLPlayer, InventoryMenuDialogHolderTemplateBuilder<B>> next) {
        this.next = (slp) -> Optional.ofNullable(next.apply(slp));
    }
    
    public void setNext(Supplier<InventoryMenuDialogHolderTemplateBuilder<B>> next) {
        this.next = (slp) -> {
            return Optional.ofNullable(next.get());
        };
    }
    
    public void setNext(InventoryMenuDialogHolderTemplateBuilder<B> next) {
        this.next = (s) -> Optional.ofNullable(next);
    }
    
    @Override
    public InventoryMenuDialogHolder construct(AbstractInventoryMenu parent, SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();
        InventoryMenuDialogHolder menu = new InventoryMenuDialogHolder(parent, is, getTitle(slp), components, staticComponents, super.getAccessController(), super.getVisibilityController(), slp, getComponentFlags(), flags, () -> next.apply(slp).map(i -> i.build()).orElse(null));
        return menu;
    }
    
}
