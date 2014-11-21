/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class PlayerLoadedEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final GeneralPlayer gp;
    
    public PlayerLoadedEvent(Player player, GeneralPlayer gp) {
        this.player = player;
        this.gp = gp;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public GeneralPlayer getGeneralPlayer() {
        return gp;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
