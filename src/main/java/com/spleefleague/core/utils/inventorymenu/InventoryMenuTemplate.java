package com.spleefleague.core.utils.inventorymenu;

import java.util.HashMap;
import java.util.Map;

import com.spleefleague.core.player.SLPlayer;
import java.util.function.Function;

public class InventoryMenuTemplate extends InventoryMenuComponentTemplate<InventoryMenu> {

    private Function<SLPlayer, String> title;

    private final Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> components, staticComponents;
    
    private int flags;

    protected InventoryMenuTemplate() {
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

    public void addComponent(int position, InventoryMenuComponentTemplate<? extends InventoryMenuComponent> component) {
        components.put(position, component);
    }

    public void addStaticComponent(int position, InventoryMenuComponentTemplate<? extends InventoryMenuComponent> component) {
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

    @Override
    public InventoryMenu construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();

        //Construct components
//        Map<Integer, InventoryMenuComponent> actualComponents = components.entrySet().stream()
//                .collect(Collectors.toMap(
//                        entry -> entry.getKey(),
//                        entry -> entry.getValue().construct(slp)));
//        Map<Integer, InventoryMenuComponent> actualStaticComponents = staticComponents.entrySet().stream()
//                .collect(Collectors.toMap(
//                        entry -> entry.getKey(),
//                        entry -> entry.getValue().construct(slp)));
        InventoryMenu menu = new InventoryMenu(is, getTitle(slp), components, staticComponents, super.getAccessController(), super.getVisibilityController(), slp, flags);
//        addMenuControls(actualComponents);
//        menu.populateInventory();
        return menu;
    }

    private void addMenuControls(Map<Integer, InventoryMenuComponent> components) {
        components.values().stream()
                .filter(comp -> comp instanceof InventoryMenu)
                .map(comp -> (InventoryMenu) comp)
                .forEach(tempMenu -> tempMenu.addMenuControls());
    }

}
