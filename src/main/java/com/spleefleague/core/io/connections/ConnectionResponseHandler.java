package com.spleefleague.core.io.connections;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.ConnectionEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.UUID;

/**
 * Created by Josh on 04/08/2016.
 */
public abstract class ConnectionResponseHandler implements Listener {

    private final String channel;
    private final JSONObject sendObject;
    private final long timeout;
    private final UUID responseID;

    private int taskID;

    public ConnectionResponseHandler(String channel, JSONObject sendObject, long timeout) {
        this.channel = channel;
        this.sendObject = sendObject;
        this.timeout = timeout;
        this.responseID = UUID.randomUUID();

        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());

        sendObject.put("responseID", responseID.toString());
        ConnectionClient cc = SpleefLeague.getInstance().getConnectionClient();
        if(cc.isEnabled()) {
            cc.send(channel, sendObject);
            this.taskID = Bukkit.getScheduler().runTaskLater(SpleefLeague.getInstance(), () -> {
                response(null);
            }, timeout).getTaskId();
        }
        else {
            this.taskID = -1;
            response(null);
        }
    }

    protected abstract void response(JSONObject jsonObject);

    @EventHandler
    public void onConnection(ConnectionEvent e) throws JSONException {
        try {
            if (e.getJSONObject().has("responseID") && e.getJSONObject().getString("responseID").equalsIgnoreCase(responseID.toString())) {
                Bukkit.getScheduler().cancelTask(taskID);
                HandlerList.unregisterAll(this);
                response((JSONObject) JSONValue.parse(e.getJSONObject().toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
