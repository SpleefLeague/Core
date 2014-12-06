/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.listeners;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.chat.ChatManager;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Jonas
 */
public class ChatListener implements Listener {
    
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), SpleefLeague.getInstance());
    }
    
    private ChatListener() {
        
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
        String prefix = "";
        if(slp.getRank() != Rank.DEFAULT) {
            prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getName() + ChatColor.DARK_GRAY + "] ";
        }
        event.setCancelled(true);
        ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), slp.getSendingChannel());
    }
}
