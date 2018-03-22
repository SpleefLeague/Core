/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplateBuilder;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogItemTemplateBuilder<B> extends InventoryMenuComponentTemplateBuilder<InventoryMenuDialogItem<B>, InventoryMenuDialogItemTemplate<B>, InventoryMenuDialogItemTemplateBuilder<B>> {

    @Override
    protected InventoryMenuDialogItemTemplateBuilder<B> getThis() {
        return this;
    }

    @Override
    protected InventoryMenuDialogItemTemplate<B> getObj() {
        return new InventoryMenuDialogItemTemplate<>();
    }
}