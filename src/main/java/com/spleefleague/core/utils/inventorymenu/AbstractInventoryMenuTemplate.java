package com.spleefleague.core.utils.inventorymenu;

import java.util.HashMap;
import java.util.Map;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;

public abstract class AbstractInventoryMenuTemplate<C extends AbstractInventoryMenu, I extends InventoryMenuComponent> extends InventoryMenuComponentTemplate<C> {

    private Function<SLPlayer, String> title;
    protected final Map<Integer, InventoryMenuComponentTemplate<? extends I>> components, staticComponents;
    
    protected int flags;

    protected AbstractInventoryMenuTemplate() {
        this.title = s -> "";
        this.components = new HashMap<>();
        this.staticComponents = new HashMap<>();
        this.flags = 0;
        this.flags = InventoryMenuFlag.set(flags, InventoryMenuFlag.EXIT_ON_CLICK_OUTSIDE);
    }

    public void setTitle(String title) {
        this.title = s -> title;
    }

    public void setTitle(Function<SLPlayer, String> title) {
        this.title = title;
    }

    public void addComponent(int position, InventoryMenuComponentTemplate<? extends I> component) {
        components.put(position, component);
    }

    public void addStaticComponent(int position, InventoryMenuComponentTemplate<? extends I> component) {
        staticComponents.put(position, component);
    }

//    public void dynamicComponents(Consumer<InventoryMenuDynamicComponents> dynamicComponents) {
//        this.dynamicComponents = dynamicComponents;
//    }
    public void addFlag(InventoryMenuFlag flag) {
        this.flags = InventoryMenuFlag.set(flags, flag);
    }

    public void removeFlag(InventoryMenuFlag flag) {
        this.flags = InventoryMenuFlag.unset(flags, flag);
    }

    public String getTitle(SLPlayer slp) {
        return title.apply(slp);
    }
}
