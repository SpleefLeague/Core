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
public class InventoryMenuDialogItemTemplateBuilder<B> extends InventoryMenuComponentTemplateBuilder<InventoryMenuDialogItem<B>, InventoryMenuDialogItemTemplate<B>, InventoryMenuDialogItemTemplateBuilder<B>> {

    public InventoryMenuDialogItemTemplateBuilder() {

    }

    public InventoryMenuDialogItemTemplateBuilder<B> next(Function<SLPlayer, InventoryMenuDialogTemplate<B>> next) {
        buildingObj.setNext(next);
        return this;
    }

    public InventoryMenuDialogItemTemplateBuilder<B> next(InventoryMenuDialogTemplate<B> next) {
        buildingObj.setNext(next);
        return this;
    }

    public InventoryMenuDialogItemTemplateBuilder<B> onClick(InventoryMenuDialogClickListener<B> listener) {
        buildingObj.setClickListener(listener);
        return this;
    }

    //Needed for Builder Inheritance
    @Override
    protected InventoryMenuDialogItemTemplateBuilder<B> getThis() {
        return this;
    }

    @Override
    protected InventoryMenuDialogItemTemplate<B> getObj() {
        return new InventoryMenuDialogItemTemplate();
    }
}