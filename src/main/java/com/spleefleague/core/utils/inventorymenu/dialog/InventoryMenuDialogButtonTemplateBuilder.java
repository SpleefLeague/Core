/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplateBuilder;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogButtonTemplateBuilder<B> extends InventoryMenuComponentTemplateBuilder<InventoryMenuDialogButton<B>, InventoryMenuDialogButtonTemplate<B>, InventoryMenuDialogButtonTemplateBuilder<B>> {

    public InventoryMenuDialogButtonTemplateBuilder() {

    }

    public InventoryMenuDialogButtonTemplateBuilder<B> next(Function<SLPlayer, InventoryMenuDialogHolderTemplateBuilder<B>> next) {
        buildingObj.setNext(slp -> next.apply(slp));
        return this;
    }

    public InventoryMenuDialogButtonTemplateBuilder<B> next(InventoryMenuDialogHolderTemplateBuilder<B> next) {
        buildingObj.setNext(next);
        return this;
    }

    public InventoryMenuDialogButtonTemplateBuilder<B> onClick(InventoryMenuDialogClickListener<B> listener) {
        buildingObj.setClickListener(listener);
        return this;
    }

    //Needed for Builder Inheritance
    @Override
    protected InventoryMenuDialogButtonTemplateBuilder<B> getThis() {
        return this;
    }

    @Override
    protected InventoryMenuDialogButtonTemplate<B> getObj() {
        return new InventoryMenuDialogButtonTemplate();
    }
}