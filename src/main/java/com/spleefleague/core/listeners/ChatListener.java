/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.events.ChatChannelMessageEvent;
import com.spleefleague.core.io.connections.ConnectionClient;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.event.EventPriority;
import org.json.simple.JSONObject;

/**
 *
 * @author Jonas
 */
public class ChatListener implements Listener {

    private static Listener instance;

    public static void init() {
        if (instance == null) {
            instance = new ChatListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private ChatListener() {

    }

    private Map<UUID, Long> lastMessage = new HashMap<>();
    private final Pattern antiCapsPattern = Pattern.compile(".*[A-Z]{4}.*");

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (!lastMessage.containsKey(slp.getUniqueId()) || System.currentTimeMillis() - lastMessage.get(slp.getUniqueId()) > 2000) {
            String message = event.getMessage();
            if(antiCapsPattern.matcher(message).matches() && !slp.getRank().hasPermission(Rank.valueOf("$")))
                event.setMessage(message.toLowerCase());
            String prefix = "";
            if (!slp.getRank().getDisplayName().equals("Default")) {
                prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getDisplayName() + ChatColor.DARK_GRAY + "] ";
            }
            if (!event.isCancelled()) {
                ChatChannel channel = slp.getSendingChannel();
                ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), channel);
            }
        }
        if (slp.getRank() != null && !(slp.getRank().hasPermission(Rank.MODERATOR) || Arrays.asList(Rank.MODERATOR).contains(slp.getRank()))) {
            lastMessage.put(slp.getUniqueId(), System.currentTimeMillis());
        }
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatChannel(ChatChannelMessageEvent event) {
        if(event.getChannel() == ChatChannel.STAFF) {
            ConnectionClient cc = SpleefLeague.getInstance().getConnectionClient();
            if(cc.isEnabled()) {
                event.setCancelled(true);
                JSONObject send = new JSONObject();
                send.put("message", event.getMessage());
                SpleefLeague.getInstance().getConnectionClient().send("staff", send);
            }
        }
    }
}
