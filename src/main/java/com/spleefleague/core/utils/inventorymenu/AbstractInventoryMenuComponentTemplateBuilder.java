package com.spleefleague.core.utils.inventorymenu;

public abstract class AbstractInventoryMenuComponentTemplateBuilder<C extends AbstractInventoryMenuComponent, T extends AbstractInventoryMenuComponentTemplate<C>, B extends AbstractInventoryMenuComponentTemplateBuilder<C, T, B>> {

    //Needed for super fancy Builder inheritance
    protected B actualBuilder;
    protected T buildingObj;

    protected abstract B getThis();

    protected abstract T getObj();

    public AbstractInventoryMenuComponentTemplateBuilder() {
        actualBuilder = getThis();
        buildingObj = getObj();
    }

    public T build() {
        return buildingObj;
    }
}
