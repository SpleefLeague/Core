/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Jonas
 */
@Deprecated
public class SlackMessageReceivedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String userid, message;

    public SlackMessageReceivedEvent(String userid, String message) {
        this.userid = userid;
        this.message = message;
    }

    public String getUserID() {
        return userid;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
