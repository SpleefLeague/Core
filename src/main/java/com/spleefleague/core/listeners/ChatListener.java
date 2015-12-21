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
import com.spleefleague.core.player.SLPlayer;
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
    
    public static void init() {
        if(instance == null) {
            instance = new ChatListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private ChatListener() {

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
        }
        event.setCancelled(true);
    }
}