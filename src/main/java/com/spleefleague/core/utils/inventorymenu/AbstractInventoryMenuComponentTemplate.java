package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;

public abstract class AbstractInventoryMenuComponentTemplate<C extends AbstractInventoryMenuComponent> {    

    public abstract C construct(AbstractInventoryMenu parent, SLPlayer slp);

}
