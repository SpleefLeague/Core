package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.events.ConnectionEvent;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.JSONException;

/**
 * Created by Josh on 21/02/2016.
 */
public class ConnectionListener implements Listener {

    private static Listener instance;

    public static void init() {
        if (instance == null) {
            instance = new ConnectionListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private ConnectionListener() {

    }

    @EventHandler
    public void onConnection(ConnectionEvent e) {
        if (e.getChannel().equalsIgnoreCase("staff")) {
            SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((SLPlayer slPlayer) -> slPlayer.isInChatChannel(ChatChannel.STAFF)).forEach((SLPlayer slPlayer) -> {
                try {
                    slPlayer.sendMessage(e.getJSONObject().getString("message"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            });
        }
    }

}
