package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.listeners.InventoryMenuListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;

public class InventoryMenuTemplate extends InventoryMenuComponentTemplate<InventoryMenu> {

    private Dynamic<String> title;

    private Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> components;

    private boolean exitOnClickOutside;

    private boolean menuControls;
    
    protected InventoryMenuTemplate() {
        this.title = Dynamic.getConstant("");
        this.components = new HashMap<>();
        this.exitOnClickOutside = true;
        this.menuControls = false;
    }

    public void setTitle(String title) {
        this.title = Dynamic.getConstant(title);
    }
    
    public void setTitle(Dynamic<String> title) {
        this.title = Dynamic.getDynamicDefault(title, "Title", "Title");
    }

    public void addComponent(int position, InventoryMenuComponentTemplate<? extends InventoryMenuComponent> component) {
        components.put(position, component);
    }

//    public void dynamicComponents(Consumer<InventoryMenuDynamicComponents> dynamicComponents) {
//        this.dynamicComponents = dynamicComponents;
//    }

    public void setExitOnClickOutside(boolean exitOnClickOutside) {
        this.exitOnClickOutside = exitOnClickOutside;
    }

    public void setMenuControls(boolean menuControls) {
        this.menuControls = menuControls;
    }
    
    public String getTitle(SLPlayer slp) {
        return title.get(slp);
    }

    @Override
    public InventoryMenu construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();

        //Construct components
        Map<Integer, InventoryMenuComponent> actualComponents = components.entrySet().stream()
                .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().construct(slp)));
        
        InventoryMenu menu = new InventoryMenu(is, getTitle(slp), actualComponents, exitOnClickOutside, menuControls, super.getAccessController(), super.getVisibilityController(), slp);
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
