package com.spleefleague.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.JSONObject;

/**
 * Created by Josh on 21/02/2016.
 */
public class ConnectionEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String channel, server;
    private final JSONObject jsonObject;

    public ConnectionEvent(String channel, String server, JSONObject jsonObject) {
        this.channel = channel;
        this.server = server;
        this.jsonObject = jsonObject;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public String getChannel() {
        return channel;
    }

    public String getOriginatingServer() {
        return server;
    }

    public JSONObject getJSONObject() {
        return jsonObject;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
