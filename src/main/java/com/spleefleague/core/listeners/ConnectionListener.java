package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.commands.ticket;
import com.spleefleague.core.events.ConnectionEvent;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.player.SLPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * Created by Josh on 21/02/2016.
 */
public class ConnectionListener implements Listener {

    private static Listener instance;

    private ConnectionListener() {

    }

    public static void init() {
        if (instance == null) {
            instance = new ConnectionListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    @EventHandler
    public void onConnection(ConnectionEvent e) throws JSONException {
        if (e.getChannel().equalsIgnoreCase("staff")) {
            SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((SLPlayer slPlayer) -> slPlayer.isInChatChannel(ChatChannel.STAFF)).forEach((SLPlayer slPlayer) -> {
                try {
                    slPlayer.sendMessage(e.getJSONObject().getString("message"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            });
        } else if (e.getChannel().equalsIgnoreCase("sessions")) {
            if (e.getJSONObject().has("action") && e.getJSONObject().getString("action").equalsIgnoreCase("REQUEST_UPDATE")) {
                UUID uuid = UUID.fromString(e.getJSONObject().getString("uuid"));
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    SLPlayer slPlayer = SpleefLeague.getInstance().getPlayerManager().get(player);

                    JSONObject send = new JSONObject();
                    send.put("uuid", slPlayer.getUniqueId().toString());
                    send.put("rank", slPlayer.getRank().getName());
                    send.put("action", "UPDATE_INFO");
                    SpleefLeague.getInstance().getConnectionClient().send("sessions", send);
                }
            }
        } else if (e.getChannel().equalsIgnoreCase("ticket")) {
            String playerName = e.getJSONObject().getString("sendName"), shownName = e.getJSONObject().getString("shownName"),
                    message = e.getJSONObject().getString("message"),
                    server = (e.getJSONObject().has("overrideServer") ? e.getJSONObject().getString("overrideServer") : e.getOriginatingServer());
            UUID playerUUID = UUID.fromString(e.getJSONObject().getString("sendUUID"));
            ChatColor chatColor = ChatColor.valueOf(e.getJSONObject().getString("rankColor").toUpperCase());
            ticket ticketCommand = (ticket)SpleefLeague.getInstance().getBasicCommand("ticket");
            ticketCommand.sendTicket(playerName, shownName, playerUUID, message, server, chatColor);
        } else if(e.getChannel().equalsIgnoreCase("broadcast")) {
            Bukkit.broadcastMessage(String.format(SpleefLeague.BROADCAST_FORMAT, e.getJSONObject().getString("message")));
        }
        
    }

    @EventHandler
    public void onPlayerJoin(GeneralPlayerLoadedEvent e) {
        if (e.getGeneralPlayer() instanceof SLPlayer) {
            SLPlayer slPlayer = (SLPlayer) e.getGeneralPlayer();

            JSONObject send = new JSONObject();
            send.put("uuid", slPlayer.getUniqueId().toString());
            send.put("rank", slPlayer.getRank().getName());
            send.put("action", "UPDATE_INFO");
            SpleefLeague.getInstance().getConnectionClient().send("sessions", send);
        }
    }

}
