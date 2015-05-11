/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.listeners;

import java.util.Iterator;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.chat.ChatChannel;
import net.spleefleague.core.chat.ChatManager;
import net.spleefleague.core.events.GeneralPlayerLoadedEvent;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
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
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
        String prefix = "";
        if(slp.getRank() != Rank.DEFAULT) {
            prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getName() + ChatColor.DARK_GRAY + "] ";
        }
        event.setCancelled(true);
        ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), slp.getSendingChannel());
    }
    
    @EventHandler
    public void onLoaded(GeneralPlayerLoadedEvent event) {
        if(event.isFirstJoin() && event.getGeneralPlayer() instanceof SLPlayer) {
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
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
        Iterator<String> i = slp.getReceivingChatChannels().iterator();
        while(i.hasNext()) {
            ChatChannel cc = ChatChannel.fromString(i.next());
            if(cc.isTemporary()) {
                slp.removeChatChannel(cc.getName());
            }
        }
    }
}
