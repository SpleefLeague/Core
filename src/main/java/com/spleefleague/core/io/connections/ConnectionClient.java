package com.spleefleague.core.io.connections;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.ConnectionEvent;
import com.spleefleague.core.io.Config;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.bukkit.Bukkit;
import org.json.JSONTokener;
import org.json.simple.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 21/02/2016.
 */
public class ConnectionClient {

    private List<JSONObject> queued;
    private Socket socket;
    private boolean enabled = false;

    public ConnectionClient() {
        if (!Config.hasKey("server_name")) {
            SpleefLeague.getInstance().getLogger().severe("Server name not set in config, shutting down connections.");
            return;
        }
        this.queued = new ArrayList<>();
        try {
            this.socket = IO.socket("http://127.0.0.1:9092");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        enabled = true;
        socket.on(Socket.EVENT_CONNECT, (Object... args) -> {
            JSONObject send = new JSONObject();
            send.put("name", Config.getString("server_name"));
            send("connect", send);
            queued.forEach((JSONObject jsonObject) -> this.socket.emit("global", jsonObject));
            queued.clear();
        }).on("global", (Object... args) -> {
            if (args.length != 2) {
                return;
            }
            try {
                org.json.JSONObject jsonObject = (org.json.JSONObject) args[0];
                if (jsonObject == null || jsonObject.length() == 0) {
                    jsonObject = new org.json.JSONObject(new JSONTokener(args[1].toString()));
                    if(jsonObject.length() == 0) {
                        return;
                    }
                }
                String channel = jsonObject.get("channel").toString(), server = jsonObject.getString("server");
                jsonObject.remove("channel");
                jsonObject.remove("server");
                Bukkit.getPluginManager().callEvent(new ConnectionEvent(channel, server, jsonObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.socket.connect();
    }

    /**
     * Handle shutdown.
     */
    public void stop() {
        if (socket != null) {
            this.socket.close();
        }
    }

    /**
     * Send a packet via SocketIO.
     *
     * @param channel channel to send the packet on.
     * @param jsonObject json object to send.
     */
    public void send(String channel, JSONObject jsonObject) {
        if(!enabled) {
            return;
        }
        jsonObject.put("channel", channel);
        jsonObject.put("server", Config.getString("server_name"));
        if(this.socket == null || !this.socket.connected()) {
            queued.add(jsonObject);
        } else {
            this.socket.emit("global", jsonObject);
        }
    }

}
