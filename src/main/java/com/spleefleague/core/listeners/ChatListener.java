/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import java.util.HashSet;
import java.util.Iterator;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.SlackApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Jonas
 */
public class ChatListener implements Listener {
    
    private static Listener instance;
    //private final SlackApi livechatWebhook;
    
    public static void init() {
        if(instance == null) {
            instance = new ChatListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private ChatListener() {
//        livechatWebhook = new SlackApi(Settings.getString("url_slack_serverchat"));
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer().getUniqueId());
        String prefix = "";
        if(!slp.getRank().getDisplayName().equals("Default")) {
            prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getDisplayName() + ChatColor.DARK_GRAY + "] ";
        }
        if(!event.isCancelled()) {
            ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), slp.getSendingChannel());
//            pushToSlack(slp.getName(), event.getMessage());
        }
        event.setCancelled(true);
    }
    /*
    @EventHandler
    public void onSlackMessage(SlackMessageReceivedEvent event) {
        Document result = SpleefLeague.getInstance().getPluginDB().getCollection("SlackUsers").find(new Document("slack_id", event.getUserID())).first();
        if(result != null) {
            UUID uuid = UUID.fromString((String) result.get("mc_uuid"));
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(player.getUniqueId());
                String prefix = "";
                if(!slp.getRank().getDisplayName().equals("Default")) {
                    prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getDisplayName() + ChatColor.DARK_GRAY + "] ";
                }
                ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), slp.getSendingChannel());
            }
            else {
                String prefix = "";
                Rank rank = DatabaseConnection.getRank(uuid);
                String name = DatabaseConnection.getUsername(uuid);
                if(!rank.getDisplayName().equals("Default")) {
                    prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + rank.getDisplayName() + ChatColor.DARK_GRAY + "] ";
                }
                ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + rank.getColor() + name + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), "DEFAULT");
            }
        }
    }
    
    private void pushToSlack(String sender, String message) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                Document json = new Document();
                json.put("username", sender);
                json.put("text", message);
                json.put("icon_url", "https://minotar.net/avatar/" + sender);
                livechatWebhook.send(json.toJson());
            }
        });
    }
    */
    @EventHandler
    public void onLoaded(GeneralPlayerLoadedEvent event) {
        if(event.getGeneralPlayer() instanceof SLPlayer) {
            SLPlayer slp = (SLPlayer)event.getGeneralPlayer();
            for(ChatChannel channel : ChatManager.getAvailableChatChannels(slp)) {
                if(channel.isDefault() && !slp.isInChatChannel(channel.getName())) {
                    slp.addChatChannel(channel.getName());
                }
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer().getUniqueId());
        Iterator<String> i = ((HashSet<String>)slp.getReceivingChatChannels().clone()).iterator();
        while(i.hasNext()) {
            String name = i.next();
            ChatChannel cc = ChatChannel.fromString(name);
            if(cc != null) {
                if(cc.isTemporary()) {
                    slp.removeChatChannel(cc.getName());
                }
            }
            else {
                slp.removeChatChannel(name);
            }
        }
    }
}