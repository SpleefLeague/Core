/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogTemplate<B> extends AbstractInventoryMenuTemplate<InventoryMenuDialog<B>, InventoryMenuDialogComponent> {

    private Function<SLPlayer, B> builderFactory;
    private BiConsumer<SLPlayer, B> completionListener;
    
    public void setBuilderFactory(Function<SLPlayer, B> builderFactory) {
        this.builderFactory = builderFactory;
    }

    public void setCompletionListener(BiConsumer<SLPlayer, B> completionListener) {
        this.completionListener = completionListener;
    }
    
    @Override
    public InventoryMenuDialog construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();
        InventoryMenuDialog menu = new InventoryMenuDialog(is, getTitle(slp), components, staticComponents, super.getAccessController(), super.getVisibilityController(), slp, flags, builderFactory.apply(slp), completionListener);
        return menu;
    }
    
}
