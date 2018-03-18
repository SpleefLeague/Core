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
public class InventoryMenuDialogTemplateBuilder<B> extends InventoryMenuComponentTemplateBuilder<InventoryMenuDialog<B>, InventoryMenuDialogTemplate<B>, InventoryMenuDialogTemplateBuilder<B>> {

    public InventoryMenuDialogTemplateBuilder() {

    }

    public InventoryMenuDialogTemplateBuilder builderFactory(Function<SLPlayer, B> builderFactory) {
        buildingObj.setBuilderFactory(builderFactory);
        return this;
    }

    @Override
    protected InventoryMenuDialogTemplateBuilder<B> getThis() {
        return this;
    }

    @Override
    protected InventoryMenuDialogTemplate<B> getObj() {
        return new InventoryMenuDialogTemplate();
    }
}