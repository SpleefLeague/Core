/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplateBuilder;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogTemplateBuilder<B> extends InventoryMenuComponentTemplateBuilder<InventoryMenuDialog<B>, InventoryMenuDialogTemplate<B>, InventoryMenuDialogTemplateBuilder<B>> {
    
    public InventoryMenuDialogTemplateBuilder<B> builder(Function<SLPlayer, B> builderFactory) {
        buildingObj.setBuilderFactory(builderFactory);
        return actualBuilder;
    }
    
    public InventoryMenuDialogTemplateBuilder<B> onDone(BiConsumer<SLPlayer, B> completionListener) {
        buildingObj.setCompletionListener(completionListener);
        return actualBuilder;
    }
    
    public InventoryMenuDialogTemplateBuilder<B>  flags(InventoryMenuDialogFlag... flags) {
        for(InventoryMenuDialogFlag flag : flags) {
            buildingObj.addFlag(flag);
        }
        return actualBuilder;
    }
    
    public InventoryMenuDialogTemplateBuilder<B>  unsetFlags(InventoryMenuDialogFlag... flags) {
        for(InventoryMenuDialogFlag flag : flags) {
            buildingObj.removeFlag(flag);
        }
        return actualBuilder;
    }
    
    public InventoryMenuDialogTemplateBuilder<B> start(InventoryMenuDialogHolderTemplateBuilder<B> start) {
        return start(start.build());
    }
    
    public InventoryMenuDialogTemplateBuilder<B> start(Function<SLPlayer, InventoryMenuDialogHolderTemplate<B>> start) {
        buildingObj.setStart(start);
        return actualBuilder;
    }
    
    public InventoryMenuDialogTemplateBuilder<B> start(InventoryMenuDialogHolderTemplate<B> start) {
        buildingObj.setStart((x) -> start);
        return actualBuilder;
    }
    
    public InventoryMenuDialogTemplateBuilder<B> start(Supplier<InventoryMenuDialogHolderTemplate<B>> start) {
        buildingObj.setStart((x) -> start.get());
        return actualBuilder;
    }

    @Override
    protected InventoryMenuDialogTemplateBuilder<B> getThis() {
        return this;
    }

    @Override
    protected InventoryMenuDialogTemplate<B> getObj() {
        return new InventoryMenuDialogTemplate<>();
    }
}