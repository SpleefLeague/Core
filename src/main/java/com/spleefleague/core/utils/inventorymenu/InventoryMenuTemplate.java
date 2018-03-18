package com.spleefleague.core.utils.inventorymenu;


import com.spleefleague.core.player.SLPlayer;

public class InventoryMenuTemplate extends AbstractInventoryMenuTemplate<InventoryMenu> {

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
}
