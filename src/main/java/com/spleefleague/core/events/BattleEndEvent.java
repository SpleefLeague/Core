/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.queue.Battle;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class BattleEndEvent extends BattleEvent {

    private static final HandlerList handlers = new HandlerList();
    private final EndReason reason;
    
    public BattleEndEvent(Battle battle, EndReason reason) {
        super(battle);
        this.reason = reason;
    }
    
    public EndReason getReason() {
        return reason;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public static enum EndReason {
        NORMAL,
        CANCEL,
        ENDGAME,
        SURRENDER,
        QUIT;
    }
}
