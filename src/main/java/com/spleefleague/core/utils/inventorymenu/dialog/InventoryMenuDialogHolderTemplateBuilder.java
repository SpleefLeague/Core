/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenuTemplateBuilder;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogHolderTemplateBuilder<B> extends AbstractInventoryMenuTemplateBuilder<InventoryMenuDialogHolder<B>, InventoryMenuDialogHolderTemplate<B>, InventoryMenuDialogHolderTemplateBuilder<B>> {

    public InventoryMenuDialogHolderTemplateBuilder() {
    
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