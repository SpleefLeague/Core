/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.events;

import net.spleefleague.core.player.GeneralPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class BattleCancelEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean successful = false;
    private final GeneralPlayer gp;
    
    public BattleCancelEvent(GeneralPlayer gp) {
        this.gp = gp;
    }
    
    public GeneralPlayer getPlayer() {
        return gp;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public boolean wasSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
