package com.spleefleague.core.utils.inventorymenu;

public class InventoryMenuTemplateBuilder extends AbstractInventoryMenuTemplateBuilder<InventoryMenu, InventoryMenuTemplate, InventoryMenuTemplateBuilder> {

    @Override
    protected InventoryMenuTemplateBuilder getThis() {
        return this;
    }

    @Override
    protected InventoryMenuTemplate getObj() {
        return new InventoryMenuTemplate();
    }
}
