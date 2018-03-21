package com.spleefleague.core.utils.inventorymenu;


import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Tuple;
import java.util.Map;
import java.util.function.Supplier;

public class InventoryMenuTemplate extends AbstractInventoryMenuTemplate<InventoryMenu> {

    @Override
    public InventoryMenu construct(SLPlayer slp) {
        ItemStackWrapper is = constructDisplayItem();
        //TODO Fix this ugly cast with generics, I honestly don't know how right now..
        Map<Integer, Tuple<Supplier<InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>>, InventoryMenuComponentAlignment>> cp = (Map<Integer, Tuple<Supplier<InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>>, InventoryMenuComponentAlignment>>)((Object)this.components);
        Map<Integer, Supplier<InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>>> scp = (Map<Integer, Supplier<InventoryMenuComponentTemplate<? extends SelectableInventoryMenuComponent>>>)((Object)this.staticComponents);
        InventoryMenu menu = new InventoryMenu(is, getTitle(slp), cp, scp, super.getAccessController(), super.getVisibilityController(), slp, flags);
        return menu;
    }
}
