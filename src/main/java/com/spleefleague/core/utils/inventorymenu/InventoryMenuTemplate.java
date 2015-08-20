package com.spleefleague.core.utils.inventorymenu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.spleefleague.core.utils.function.PlayerToValueMapper;

public class InventoryMenuTemplate extends InventoryMenuComponentTemplate<InventoryMenu> {

    private String title;
    private PlayerToValueMapper<String> titlePlayerSpecific;

    private Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> components;
    private Consumer<InventoryMenuDynamicComponents> dynamicComponents;

    private boolean exitOnClickOutside;

    private boolean menuControls;

    InventoryMenuTemplate() {
        this.title = "";
        this.components = new HashMap<>();
        this.exitOnClickOutside = true;
        this.menuControls = false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addComponent(int position, InventoryMenuComponentTemplate<? extends InventoryMenuComponent> component) {
        components.put(position, component);
    }

    public void dynamicComponents(Consumer<InventoryMenuDynamicComponents> dynamicComponents) {
        this.dynamicComponents = dynamicComponents;
    }

    public void setExitOnClickOutside(boolean exitOnClickOutside) {
        this.exitOnClickOutside = exitOnClickOutside;
    }

    public void setMenuControls(boolean menuControls) {
        this.menuControls = menuControls;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleFor(Player p) {
        return titlePlayerSpecific != null ? titlePlayerSpecific.toValue(p) : title;
    }

    @Override
    public InventoryMenu construct() {
        ItemStack is = constructDisplayItem();

        //Construct components
        Map<Integer, InventoryMenuComponent> actualComponents = components.entrySet().stream()
                .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().construct()));

        if (dynamicComponents != null) {
            InventoryMenuDynamicComponents dynamic = new InventoryMenuDynamicComponents();

            dynamicComponents.accept(dynamic);

            Map<Integer, InventoryMenuComponent> dynamicComponents = dynamic.getComponents().entrySet().stream()
                    .collect(Collectors.toMap(
                                    entry -> entry.getKey(),
                                    entry -> entry.getValue().construct()));

            actualComponents.putAll(dynamicComponents);

        }

        InventoryMenu menu = new InventoryMenu(is, title, actualComponents, exitOnClickOutside, menuControls);

        addMenuControls(actualComponents);

        return menu;
    }

    @Override
    public InventoryMenu constructFor(Player p) {
        ItemStack is = constructDisplayItemFor(p);

        //Construct components
        Map<Integer, InventoryMenuComponent> actualComponents = components.entrySet().stream()
                .collect(Collectors.toMap(
                                entry -> entry.getKey(),
                                entry -> entry.getValue().constructFor(p)));

        if (dynamicComponents != null) {
            InventoryMenuDynamicComponents dynamic = new InventoryMenuDynamicComponents();

            dynamicComponents.accept(dynamic);

            Map<Integer, InventoryMenuComponent> dynamicComponents = dynamic.getComponents().entrySet().stream()
                    .collect(Collectors.toMap(
                                    entry -> entry.getKey(),
                                    entry -> entry.getValue().constructFor(p)));

            actualComponents.putAll(dynamicComponents);

        }

        InventoryMenu menu = new InventoryMenu(is, getTitleFor(p), actualComponents, exitOnClickOutside, menuControls);
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
