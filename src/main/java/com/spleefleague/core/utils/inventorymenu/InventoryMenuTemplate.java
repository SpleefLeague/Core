package com.spleefleague.core.utils.inventorymenu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;

public class InventoryMenuTemplate extends InventoryMenuComponentTemplate<InventoryMenu> {

    private Dynamic<String> title;

    private Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> components;
    private Consumer<InventoryMenuDynamicComponents> dynamicComponents;

    private boolean exitOnClickOutside;

    private boolean menuControls;
    
    private Dynamic<Boolean> accessController;
    
    protected InventoryMenuTemplate() {
        this.title = Dynamic.getConstant("");
        this.components = new HashMap<>();
        this.exitOnClickOutside = true;
        this.menuControls = false;
        this.accessController = (SLPlayer slp) -> slp.getRank().hasPermission(Rank.DEFAULT);
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

    public String getTitle() {
        return getTitle(null);
    }

    public String getTitle(SLPlayer slp) {
        return title.get(slp);
    }
    
    public void setRank(Rank rank){
        this.accessController = (SLPlayer slp) -> slp.getRank().hasPermission(Rank.DEFAULT);
    }
    
    public void setAccessController(Dynamic<Boolean> accessController) {
        this.accessController = accessController;
    }

    @Override
    public InventoryMenu construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();

        //Construct components
        Map<Integer, InventoryMenuComponent> actualComponents = components.entrySet().stream()
                .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().construct(slp)));

        if (dynamicComponents != null) {
            InventoryMenuDynamicComponents dynamic = new InventoryMenuDynamicComponents();

            dynamicComponents.accept(dynamic);

            Map<Integer, InventoryMenuComponent> dynamicComponents = dynamic.getComponents().entrySet().stream()
                    .collect(Collectors.toMap(
                                    entry -> entry.getKey(),
                                    entry -> entry.getValue().construct(slp)));

            actualComponents.putAll(dynamicComponents);

        }

        InventoryMenu menu = new InventoryMenu(is, getTitle(), actualComponents, exitOnClickOutside, menuControls, accessController, slp);

        addMenuControls(actualComponents);

        return menu;
    }

    private void addMenuControls(Map<Integer, InventoryMenuComponent> components) {
        components.values().stream()
                .filter(comp -> comp instanceof InventoryMenu)
                .map(comp -> (InventoryMenu) comp)
                .forEach(tempMenu -> tempMenu.addMenuControls());
    }

}
