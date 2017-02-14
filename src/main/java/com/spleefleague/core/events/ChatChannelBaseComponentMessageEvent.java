/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.player.SLPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
public class ChatChannelBaseComponentMessageEvent extends ChatChannelEvent {

    private static final HandlerList handlers = new HandlerList();
    private BaseComponent[] message;
    private SLPlayer sender;
    
    
    public ChatChannelBaseComponentMessageEvent(ChatChannel channel, BaseComponent[] message) {
        this(channel, message, null);
    }
    
    public ChatChannelBaseComponentMessageEvent(ChatChannel channel, BaseComponent[] message, SLPlayer sender) {
        super(channel);
        this.message = message;
        this.sender = sender;
    }
    
    public SLPlayer getSender() {
        return sender;
    }
    
    public void setSender(SLPlayer sender) {
        this.sender = sender;
    }
    
    public BaseComponent[] getMessage() {
        return message;
    }
    
    public void setMessage(BaseComponent[] message) {
        this.message = message;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
