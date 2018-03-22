package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractInventoryMenuTemplateBuilder<C extends AbstractInventoryMenu<X>, X extends InventoryMenuComponent, T extends AbstractInventoryMenuTemplate<C, X>, B extends AbstractInventoryMenuTemplateBuilder<C, X, T, B>> extends InventoryMenuComponentTemplateBuilder<C, T, B> {

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
    
    /*
        WITHOUT SUPPLIER
    */
    
    public B component(int x, int y, Supplier<AbstractInventoryMenuComponentTemplate<? extends X>> template) {
        return component(y * ROWSIZE + x, template);
    }
    
    public B component(InventoryMenuComponentAlignment align, Supplier<AbstractInventoryMenuComponentTemplate<? extends X>> template) {
        buildingObj.addComponent(--dynamic, template, align);
        return (B)this;
    }
    
    public B component(Supplier<AbstractInventoryMenuComponentTemplate<? extends X>> template) {
        return component(--dynamic, template);
    }
    
    public B component(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends X>> template) {
        buildingObj.addComponent(position, template);
        return (B)this;
    }
    
    /*
        WITHOUT SUPPLIER
    */
    
        /*
            BUILDER
        */
    public B component(int x, int y, AbstractInventoryMenuComponentTemplateBuilder<? extends X, ? extends AbstractInventoryMenuComponentTemplate<? extends X>, ?> builder) {
        return component(y * ROWSIZE + x, builder.build());
    }
    
    public B component(InventoryMenuComponentAlignment align, AbstractInventoryMenuComponentTemplateBuilder<? extends X, ? extends AbstractInventoryMenuComponentTemplate<? extends X>, ?> builder) {
        return component(align, builder.build());
    }
    
    public B component(AbstractInventoryMenuComponentTemplateBuilder<? extends X, ? extends AbstractInventoryMenuComponentTemplate<? extends X>, ?> builder) {
        return component(builder.build());
    }
    
    public B component(int position, AbstractInventoryMenuComponentTemplateBuilder<? extends X, ? extends AbstractInventoryMenuComponentTemplate<? extends X>, ?> builder) {
        return component(position, builder.build());
    }
    
        /*
            TEMPLATES
        */
    public B component(int x, int y, AbstractInventoryMenuComponentTemplate<? extends X> template) {
        return component(y * ROWSIZE + x, template);
    }
    
    public B component(AbstractInventoryMenuComponentTemplate<? extends X> template) {
        return component(--dynamic, template);
    }
    
    public B component(InventoryMenuComponentAlignment align, AbstractInventoryMenuComponentTemplate<? extends X> template) {
        buildingObj.addComponent(--dynamic, template, align);
        return (B)this;
    }
    
    public B component(int position, AbstractInventoryMenuComponentTemplate<? extends X> template) {
        buildingObj.addComponent(position, template);
        return (B)this;
    }
    
    public B staticComponent(int x, int y, Supplier<AbstractInventoryMenuComponentTemplate<? extends X>> template) {
        return staticComponent(y * ROWSIZE + x, template);
    }

    public B staticComponent(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends X>> template) {
        buildingObj.addStaticComponent(position, template);
        return (B)this;
    }
    
    public B staticComponent(int x, int y, AbstractInventoryMenuComponentTemplateBuilder<? extends X, ? extends AbstractInventoryMenuComponentTemplate<? extends X>, ?> builder) {
        return staticComponent(x, y, builder.build());
    }
    
    public B staticComponent(int position, AbstractInventoryMenuComponentTemplateBuilder<? extends X, ? extends AbstractInventoryMenuComponentTemplate<? extends X>, ?> builder) {
        return staticComponent(position, builder.build());
    }

    public B staticComponent(int x, int y, AbstractInventoryMenuComponentTemplate<? extends X> template) {
        return staticComponent(y * ROWSIZE + x, template);
    }

    public B staticComponent(int position, AbstractInventoryMenuComponentTemplate<? extends X> template) {
        buildingObj.addStaticComponent(position, template);
        return (B)this;
    }
    
    public B flags(InventoryMenuFlag... flags) {
        for(InventoryMenuFlag flag : flags) {
            buildingObj.addFlag(flag);
        }
        return (B)this;
    }
    
    public B unsetFlags(InventoryMenuFlag... flags) {
        for(InventoryMenuFlag flag : flags) {
            buildingObj.removeFlag(flag);
        }
        return (B)this;
    }
}
