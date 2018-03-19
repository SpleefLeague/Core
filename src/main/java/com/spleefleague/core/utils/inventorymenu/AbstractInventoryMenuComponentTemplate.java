package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;

public abstract class AbstractInventoryMenuComponentTemplate<C> {    

    public abstract C construct(SLPlayer slp);

}
