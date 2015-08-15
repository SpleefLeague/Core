/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.player.GeneralPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class GeneralPlayerLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final GeneralPlayer gp;
    private final boolean firstJoin;
    
    public GeneralPlayerLoadedEvent(GeneralPlayer gp, boolean firstJoin) {
        this.gp = gp;
        this.firstJoin = firstJoin;
    }
    
    public GeneralPlayer getGeneralPlayer() {
        return gp;
    }
    
    public boolean isFirstJoin() {
        return firstJoin;
    }
    
    public Player getPlayer() {
        return gp.getPlayer();
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
