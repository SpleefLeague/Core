/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.queue.Battle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 *
 * @author Jonas
 */
public abstract class BattleEvent extends Event implements Cancellable {
    
    private final Battle battle;
    private boolean cancelled = false;
    
    public BattleEvent(Battle battle) {
        this.battle = battle;
    }
    
    public Battle getBattle() {
        return battle;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
}
