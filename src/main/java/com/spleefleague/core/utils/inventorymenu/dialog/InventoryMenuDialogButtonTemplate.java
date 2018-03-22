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
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogButtonTemplate<B> extends InventoryMenuComponentTemplate<InventoryMenuDialogButton<B>>{

    private Function<SLPlayer, Optional<InventoryMenuDialogHolderTemplateBuilder<B>>> next;
    private InventoryMenuDialogClickListener<B> listener;
    
    public InventoryMenuDialogButtonTemplate() {
        super();
        this.next = (x) -> Optional.empty();
        this.listener = (e) -> e.getBuilder();
    }
    
    public void setNext(Function<SLPlayer, InventoryMenuDialogHolderTemplateBuilder<B>> next) {
        this.next = (slp) -> Optional.ofNullable(next.apply(slp));
    }
    
    public void setNext(InventoryMenuDialogHolderTemplateBuilder<B> next) {
        this.next = (s) -> Optional.ofNullable(next);
    }
    
    public void setClickListener(InventoryMenuDialogClickListener<B> listener) {
        this.listener = listener;
    }
    
    @Override
    public InventoryMenuDialogButton<B> construct(AbstractInventoryMenu parent, SLPlayer slp) {
        ItemStackWrapper isw = constructDisplayItem();
        return new InventoryMenuDialogButton(parent, isw, getVisibilityController(), getAccessController(), getOverwritePageBehavior(), listener, () -> next.apply(slp).map(i -> i.build()).orElse(null));
    }
}
