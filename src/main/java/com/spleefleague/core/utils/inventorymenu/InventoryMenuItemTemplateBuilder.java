package com.spleefleague.core.utils.inventorymenu;

public class InventoryMenuItemTemplateBuilder extends InventoryMenuComponentTemplateBuilder<InventoryMenuItem, InventoryMenuItemTemplate, InventoryMenuItemTemplateBuilder> {

    public InventoryMenuItemTemplateBuilder() {

    }

    public InventoryMenuItemTemplateBuilder onClick(InventoryMenuClickListener onClick) {
        buildingObj.setOnClick(onClick);
        return this;
    }

	//Needed for Builder Inheritance
    @Override
    protected InventoryMenuItemTemplateBuilder getThis() {
        return this;
    }

    @Override
    protected InventoryMenuItemTemplate getObj() {
        return new InventoryMenuItemTemplate();
    }
}
