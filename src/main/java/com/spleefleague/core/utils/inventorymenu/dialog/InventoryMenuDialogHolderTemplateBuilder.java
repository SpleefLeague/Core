/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenuTemplateBuilder;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogHolderTemplateBuilder<B> extends AbstractInventoryMenuTemplateBuilder<InventoryMenuDialogHolder<B>, InventoryMenuDialogHolderTemplate<B>, InventoryMenuDialogHolderTemplateBuilder<B>> {

    public InventoryMenuDialogHolderTemplateBuilder() {
    
    }

    public InventoryMenuDialogHolderTemplateBuilder<B> next(Function<SLPlayer, InventoryMenuDialogHolderTemplateBuilder<B>> next) {
        buildingObj.setNext(slp -> next.apply(slp));
        return this;
    }

    public InventoryMenuDialogHolderTemplateBuilder<B> next(InventoryMenuDialogHolderTemplateBuilder<B> next) {
        buildingObj.setNext(next);
        return this;
    }

    public InventoryMenuDialogHolderTemplateBuilder<B> next(Supplier<InventoryMenuDialogHolderTemplateBuilder<B>> next) {
        buildingObj.setNext(next);
        return this;
    }

    @Override
    protected InventoryMenuDialogHolderTemplateBuilder<B> getThis() {
        return this;
    }

    @Override
    protected InventoryMenuDialogHolderTemplate<B> getObj() {
        return new InventoryMenuDialogHolderTemplate();
    }
}