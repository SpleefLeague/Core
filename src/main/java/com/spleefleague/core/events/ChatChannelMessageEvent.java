/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class ChatChannelMessageEvent extends ChatChannelEvent {

    private static final HandlerList handlers = new HandlerList();
    private String message;
    private SLPlayer sender;
    
    
    public ChatChannelMessageEvent(ChatChannel channel, String message) {
        this(channel, message, null);
    }
    
    public ChatChannelMessageEvent(ChatChannel channel, String message, SLPlayer sender) {
        super(channel);
        this.message = message;
    }
    
    public SLPlayer getSender() {
        return sender;
    }
    
    public void setSender(SLPlayer sender) {
        this.sender = sender;
    }
    
    public String getMessage() {
        return message;
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
}
