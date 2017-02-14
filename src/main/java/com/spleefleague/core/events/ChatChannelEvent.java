/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import com.spleefleague.core.chat.ChatChannel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 *
 * @author Jonas
 */
public abstract class ChatChannelEvent extends Event implements Cancellable {

    private ChatChannel channel;
    private boolean cancelled = false;

    public ChatChannelEvent(ChatChannel channel) {
        this.channel = channel;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public void setChannel(ChatChannel channel) {
        this.channel = channel;
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