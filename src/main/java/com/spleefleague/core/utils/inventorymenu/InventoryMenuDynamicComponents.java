package com.spleefleague.core.utils.inventorymenu;

import java.util.HashMap;
import java.util.Map;

public class InventoryMenuDynamicComponents {

    private static final int ROWSIZE = 9;
    private int autoaligned = 0;
    
    private Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> components;

    public InventoryMenuDynamicComponents() {
        this.components = new HashMap<>();
    }

    public void component(int x, int y, InventoryMenuItemTemplateBuilder itemBuilder) {
        component(x, y, itemBuilder);
    }

    public void component(int x, int y, InventoryMenuItemTemplate item) {
        component(y * ROWSIZE + x, item);
    }

    public void component(int position, InventoryMenuItemTemplateBuilder itemBuilder) {
        component(position, itemBuilder.build());
    }

    public void component(int position, InventoryMenuItemTemplate item) {
        components.put(position, item);
    }

    public void component(int x, int y, InventoryMenuTemplateBuilder menuBuilder) {
        component(x, y, menuBuilder.build());
    }

    public void component(int x, int y, InventoryMenuTemplate menu) {
        component(y * ROWSIZE + x, menu);
    }

    public void component(int position, InventoryMenuTemplateBuilder menuBuilder) {
        component(position, menuBuilder.build());
    }

    public void component(int position, InventoryMenuTemplate menu) {
        components.put(position, menu);
    }
    
    public void component(InventoryMenuTemplate menu) {
        components.put(--autoaligned, menu);
    }

    public void component(InventoryMenuTemplateBuilder menuBuilder) {
        component(menuBuilder.build());
    }

    protected Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> getComponents() {
        return components;
    }

}
