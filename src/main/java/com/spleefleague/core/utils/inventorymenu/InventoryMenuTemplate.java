package com.spleefleague.core.utils.inventorymenu;


import com.spleefleague.core.player.SLPlayer;
import java.util.Map;

public class InventoryMenuTemplate extends AbstractInventoryMenuTemplate<InventoryMenu> {

    @Override
    public InventoryMenu construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();
        //TODO Fix this ugly cast with generics, I honestly don't know how right now..
        Map<Integer, InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>> cp = (Map<Integer, InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>>)((Object)this.components);
        Map<Integer, InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>> scp = (Map<Integer, InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>>)((Object)this.staticComponents);
        InventoryMenu menu = new InventoryMenu(is, getTitle(slp), cp, scp, super.getAccessController(), super.getVisibilityController(), slp, flags);
        return menu;
    }
}
