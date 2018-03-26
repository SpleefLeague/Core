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
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogTemplate<B> extends InventoryMenuComponentTemplate<InventoryMenuDialog<B>> {

    
    private Function<SLPlayer, B> builderFactory;
    private Function<SLPlayer, InventoryMenuDialogHolderTemplate<B>> start;
    private BiConsumer<SLPlayer, B> completionListener;
    private int flags = 0;
    
    public InventoryMenuDialogTemplate() {
        builderFactory = (slp) -> null;
        completionListener = (slp, b) -> {};
        start = (slp) -> null;
    }
    
    public void setBuilderFactory(Function<SLPlayer, B> builderFactory) {
        this.builderFactory = builderFactory;
    }

    public void setCompletionListener(BiConsumer<SLPlayer, B> completionListener) {
        this.completionListener = completionListener;
    }

    public void setStart(Function<SLPlayer, InventoryMenuDialogHolderTemplate<B>> start) {
        this.start = start;
    }
    
    protected int getDialogFlags() {
        return flags;
    }
    
    protected void addFlag(InventoryMenuDialogFlag flag) {
        this.flags = InventoryMenuDialogFlag.set(flags, flag);
    }

    protected void removeFlag(InventoryMenuDialogFlag flag) {
        this.flags = InventoryMenuDialogFlag.unset(flags, flag);
    }
    
    @Override
    public InventoryMenuDialog<B> construct(AbstractInventoryMenu parent, SLPlayer slp) {
        B builder = builderFactory.apply(slp);
        ItemStackWrapper isw = constructDisplayItem();
        return new InventoryMenuDialog<>(parent, isw, getVisibilityController(), getAccessController(), getComponentFlags(), getDialogFlags(), builder, slp, completionListener, start.apply(slp));
    }
}
