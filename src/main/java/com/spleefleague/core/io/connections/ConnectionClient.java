package com.spleefleague.core.io.connections;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.ConnectionEvent;
import com.spleefleague.core.io.Config;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Josh on 21/02/2016.
 */
public class ConnectionClient {

    private Socket socket;

    public ConnectionClient() {
        if(!Config.hasKey("server_name")) {
            SpleefLeague.getInstance().getLogger().severe("Server name not set in config, shutting down connections.");
            return;
        }
        try {
            this.socket = IO.socket("http://127.0.0.1:9092");
        } catch (URISyntaxException e) {
            Bukkit.getServer().shutdown();
            return;
        }
        socket.on(Socket.EVENT_CONNECT, (Object... args) -> {
            JSONObject send = new JSONObject();
            send.put("name", Config.getString("server_name"));
            send("connect", send);
        }).on("global", (Object... args) -> {
            if (args.length != 1) {
                return;
            }
            try {
                org.json.JSONObject jsonObject = (org.json.JSONObject) args[0];
                if (jsonObject == null || jsonObject.length() == 0) {
                    return;
                }
                String channel = jsonObject.getString("channel"), server = jsonObject.getString("server");
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
        if(socket != null) {
            this.socket.close();
        }
    }

    /**
     * Send a packet via SocketIO.
     *
     * @param channel    channel to send the packet on.
     * @param jsonObject json object to send.
     */
    public void send(String channel, JSONObject jsonObject) {
        jsonObject.put("channel", channel);
        jsonObject.put("server", Config.getString("server_name"));
        this.socket.emit("global", jsonObject);
    }


}
