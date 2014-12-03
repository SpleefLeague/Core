/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class ChatChannelMessageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private String channel, message;
    private boolean cancelled = false;
    
    public ChatChannelMessageEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
