package com.spleefleague.core.utils.inventorymenu;

import java.util.function.Consumer;

import com.spleefleague.core.player.Rank;
import com.spleefleague.core.utils.function.Dynamic;

public class InventoryMenuTemplateBuilder extends InventoryMenuComponentTemplateBuilder<InventoryMenu, InventoryMenuTemplate, InventoryMenuTemplateBuilder> {

    private static final int ROWSIZE = 9;
    private int dynamic = -1000;
    
    
    protected InventoryMenuTemplateBuilder() {

    }

    public InventoryMenuTemplateBuilder title(String title) {
        buildingObj.setTitle(title);
        return this;
    }
    
    public InventoryMenuTemplateBuilder title(Dynamic<String> title) {
        buildingObj.setTitle(title);
        return this;
    }

    public InventoryMenuTemplateBuilder component(int x, int y, InventoryMenuItemTemplateBuilder itemTemplateBuilder) {
        return component(x, y, itemTemplateBuilder.build());
    }

    public InventoryMenuTemplateBuilder component(int x, int y, InventoryMenuItemTemplate itemTemplate) {
        return component(y * ROWSIZE + x, itemTemplate);
    }

    public InventoryMenuTemplateBuilder component(int position, InventoryMenuItemTemplateBuilder itemTemplateBuilder) {
        return component(position, itemTemplateBuilder.build());
    }

    public InventoryMenuTemplateBuilder component(int position, InventoryMenuItemTemplate itemTemplate) {
        buildingObj.addComponent(position, itemTemplate);
        return this;
    }
    
    public InventoryMenuTemplateBuilder component(InventoryMenuItemTemplate menuItemTemplate) {
        return component(--dynamic, menuItemTemplate);
    }
    
    public InventoryMenuTemplateBuilder component(InventoryMenuItemTemplateBuilder menuItemTemplateBuilder) {
        return component(--dynamic, menuItemTemplateBuilder.build());
    }

    public InventoryMenuTemplateBuilder component(int x, int y, InventoryMenuTemplateBuilder menuTemplateBuilder) {
        return component(x, y, menuTemplateBuilder.build());
    }

    public InventoryMenuTemplateBuilder component(int x, int y, InventoryMenuTemplate menuTemplate) {
        return component(y * ROWSIZE + x, menuTemplate);
    }

    public InventoryMenuTemplateBuilder component(int position, InventoryMenuTemplateBuilder menuTemplateBuilder) {
        return component(position, menuTemplateBuilder.build());
    }

    public InventoryMenuTemplateBuilder component(int position, InventoryMenuTemplate menuTemplate) {
        buildingObj.addComponent(position, menuTemplate);
        return this;
    }
    
    public InventoryMenuTemplateBuilder component(InventoryMenuTemplate menuTemplate) {
        return component(++dynamic, menuTemplate);
    }
    
    public InventoryMenuTemplateBuilder component(InventoryMenuTemplateBuilder menuTemplateBuilder) {
        return component(++dynamic, menuTemplateBuilder.build());
    }
    
    public InventoryMenuTemplateBuilder exitOnClickOutside(boolean exitOnClickOutside) {
        buildingObj.setExitOnClickOutside(exitOnClickOutside);
        return this;
    }

    public InventoryMenuTemplateBuilder menuControls(boolean menuControls) {
        buildingObj.setMenuControls(menuControls);
        return this;
    }
    
    @Override
    protected InventoryMenuTemplateBuilder getThis() {
        return this;
    }

    @Override
    protected InventoryMenuTemplate getObj() {
        return new InventoryMenuTemplate();
    }
}
