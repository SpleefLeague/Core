/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.player.GeneralPlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author Jonas
 */
public class GeneralPlayerLoadedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final GeneralPlayer gp;
    private final boolean firstJoin;

    public GeneralPlayerLoadedEvent(GeneralPlayer gp, boolean firstJoin) {
        super(gp.getPlayer());
        this.gp = gp;
        this.firstJoin = firstJoin;
    }

    public GeneralPlayer getGeneralPlayer() {
        return gp;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
