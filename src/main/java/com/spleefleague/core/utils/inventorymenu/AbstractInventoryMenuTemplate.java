package com.spleefleague.core.utils.inventorymenu;

import java.util.HashMap;
import java.util.Map;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Tuple;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractInventoryMenuTemplate<C extends AbstractInventoryMenu<T>, T extends InventoryMenuComponent> extends InventoryMenuComponentTemplate<C> {

    private Function<SLPlayer, String> title;
    protected final Map<Integer, Tuple<Supplier<AbstractInventoryMenuComponentTemplate<? extends T>>, InventoryMenuComponentAlignment>> components;
    protected final Map<Integer, Supplier<AbstractInventoryMenuComponentTemplate<? extends T>>> staticComponents;
    
    protected int flags;

    protected AbstractInventoryMenuTemplate() {
        this.title = s -> "";
        this.components = new HashMap<>();
        this.staticComponents = new HashMap<>();
        this.flags = 0;
        this.flags = InventoryMenuFlag.set(flags, InventoryMenuFlag.EXIT_ON_CLICK_OUTSIDE);
        this.flags = InventoryMenuFlag.set(flags, InventoryMenuFlag.HIDE_EMPTY_SUBMENU);
    }

    public void setTitle(String title) {
        this.title = s -> title;
    }

    public void setTitle(Function<SLPlayer, String> title) {
        this.title = title;
    }

    public void addComponent(int position, AbstractInventoryMenuComponentTemplate<? extends T> component) {
        addComponent(position, () -> component);
    }

    public void addComponent(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends T>> component) {
        addComponent(position, component, InventoryMenuComponentAlignment.DEFAULT);
    }

    public void addComponent(int position, AbstractInventoryMenuComponentTemplate<? extends T> component, InventoryMenuComponentAlignment alignment) {
        addComponent(position, () -> component, alignment);
    }

    public void addComponent(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends T>> component, InventoryMenuComponentAlignment alignment) {
        components.put(position, new Tuple<>(component, alignment));
    }

    public void addStaticComponent(int position, AbstractInventoryMenuComponentTemplate<? extends T> component) {
        addStaticComponent(position, () -> component);
    }

    public void addStaticComponent(int position, Supplier<AbstractInventoryMenuComponentTemplate<? extends T>> component) {
        staticComponents.put(position, component);
    }
    
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
