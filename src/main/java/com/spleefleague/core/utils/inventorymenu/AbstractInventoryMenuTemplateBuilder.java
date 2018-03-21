package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractInventoryMenuTemplateBuilder<C extends AbstractInventoryMenu, T extends AbstractInventoryMenuTemplate<C>, B extends AbstractInventoryMenuTemplateBuilder<C, T, B>> extends InventoryMenuComponentTemplateBuilder<C, T, B> {

    private static final int ROWSIZE = 9;
    private int dynamic = -1000;

    protected AbstractInventoryMenuTemplateBuilder() {

    }

    public B title(String title) {
        buildingObj.setTitle(title);
        return (B)this;
    }

    public B title(Function<SLPlayer, String> title) {
        buildingObj.setTitle(title);
        return (B)this;
    }

    public B component(int x, int y, Supplier<AbstractInventoryMenuComponentTemplate<? extends C>> itemTemplate) {
        return component(y * ROWSIZE + x, itemTemplate);
    }

    public B component(Supplier<AbstractInventoryMenuComponentTemplate<? extends C>> menuItemTemplate) {
        return component(--dynamic, menuItemTemplate);
    }

    public B component(InventoryMenuComponentAlignment alignment, Supplier<AbstractInventoryMenuComponentTemplate<? extends C>> menuItemTemplate) {
        buildingObj.addComponent(--dynamic, menuItemTemplate, alignment);
        return (B)this;
    }

    public B component(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends C>> itemTemplate) {
        buildingObj.addComponent(position, itemTemplate);
        return (B)this;
    }

    public B component(int x, int y, AbstractInventoryMenuComponentTemplateBuilder itemTemplateBuilder) {
        return component(x, y, itemTemplateBuilder.build());
    }

    public B component(int x, int y, AbstractInventoryMenuComponentTemplate itemTemplate) {
        return component(y * ROWSIZE + x, itemTemplate);
    }

    public B component(int position, AbstractInventoryMenuComponentTemplateBuilder itemTemplateBuilder) {
        return component(position, itemTemplateBuilder.build());
    }

    public B component(AbstractInventoryMenuComponentTemplate menuItemTemplate) {
        return component(--dynamic, menuItemTemplate);
    }

    public B component(AbstractInventoryMenuComponentTemplateBuilder menuItemTemplateBuilder) {
        return component(--dynamic, menuItemTemplateBuilder.build());
    }

    public B component(InventoryMenuComponentAlignment alignment, AbstractInventoryMenuComponentTemplateBuilder menuItemTemplateBuilder) {
        return component(alignment, menuItemTemplateBuilder.build());
    }

    public B component(InventoryMenuComponentAlignment alignment, AbstractInventoryMenuComponentTemplate menuItemTemplate) {
        buildingObj.addComponent(--dynamic, menuItemTemplate, alignment);
        return (B)this;
    }

    public B component(int position, AbstractInventoryMenuComponentTemplate itemTemplate) {
        buildingObj.addComponent(position, itemTemplate);
        return (B)this;
    }
    
    public B component(int x, int y, AbstractInventoryMenuTemplateBuilder<C, T, B> menuTemplateBuilder) {
        return component(x, y, menuTemplateBuilder.build());
    }
    
    public B component(int x, int y, AbstractInventoryMenuTemplate<C> menuTemplate) {
        return component(y * ROWSIZE + x, menuTemplate);
    }

    public B component(int position, AbstractInventoryMenuTemplateBuilder<C, T, B> menuTemplateBuilder) {
        return component(position, menuTemplateBuilder.build());
    }

    public B component(int position, AbstractInventoryMenuTemplate<C> menuTemplate) {
        buildingObj.addComponent(position, menuTemplate);
        return (B)this;
    }

    public B component(InventoryMenuComponentAlignment alignment, AbstractInventoryMenuTemplateBuilder<C, T, B> menuTemplateBuilder) {
        return component(alignment, menuTemplateBuilder.build());
    }

    public B component(InventoryMenuComponentAlignment alignment, AbstractInventoryMenuTemplate<C> menuTemplate) {
        buildingObj.addComponent(--dynamic, menuTemplate, alignment);
        return (B)this;
    }

    public B component(AbstractInventoryMenuTemplate<C> menuTemplate) {
        return component(++dynamic, menuTemplate);
    }

    public B component(AbstractInventoryMenuTemplateBuilder<C, T, B> menuTemplateBuilder) {
        return component(++dynamic, menuTemplateBuilder.build());
    }

    public B staticComponent(int x, int y, Supplier<AbstractInventoryMenuComponentTemplate<? extends C>> menuTemplate) {
        return staticComponent(y * ROWSIZE + x, menuTemplate);
    }

    public B staticComponent(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends C>> menuTemplate) {
        buildingObj.addStaticComponent(position, menuTemplate);
        return (B)this;
    }
    
    public B staticComponent(int x, int y, AbstractInventoryMenuComponentTemplateBuilder itemTemplateBuilder) {
        return staticComponent(x, y, itemTemplateBuilder.build());
    }

    public B staticComponent(int x, int y, AbstractInventoryMenuComponentTemplate itemTemplate) {
        return staticComponent(y * ROWSIZE + x, itemTemplate);
    }

    public B staticComponent(int position, AbstractInventoryMenuComponentTemplateBuilder itemTemplateBuilder) {
        return staticComponent(position, itemTemplateBuilder.build());
    }

    public B staticComponent(int position, AbstractInventoryMenuComponentTemplate itemTemplate) {
        buildingObj.addStaticComponent(position, itemTemplate);
        return (B)this;
    }

    public B staticComponent(int x, int y, AbstractInventoryMenuTemplateBuilder<C, T, B> menuTemplateBuilder) {
        return staticComponent(x, y, menuTemplateBuilder.build());
    }

    public B staticComponent(int x, int y, AbstractInventoryMenuTemplate<C> menuTemplate) {
        return staticComponent(y * ROWSIZE + x, menuTemplate);
    }

    public B staticComponent(int position, AbstractInventoryMenuTemplateBuilder<C, T, B> menuTemplateBuilder) {
        return staticComponent(position, menuTemplateBuilder.build());
    }

    public B staticComponent(int position, AbstractInventoryMenuTemplate<C> menuTemplate) {
        buildingObj.addStaticComponent(position, menuTemplate);
        return (B)this;
    }
    
    public B flags(InventoryMenuFlag... flags) {
        for(InventoryMenuFlag flag : flags) {
            buildingObj.addFlag(flag);
        }
        return (B)this;
    }
}
