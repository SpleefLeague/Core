/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.chat;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.ChatChannelMessageEvent;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;

/**
 *
 * @author Jonas
 */
public class ChatManager {
    
    public static void sendMessage(String message, String channel) {
        ChatChannelMessageEvent ccme = new ChatChannelMessageEvent(channel, message);
        Bukkit.getPluginManager().callEvent(ccme);
        message = ccme.getMessage();
        channel = ccme.getChannel();
        if(!ccme.isCancelled()) {
            for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                if(slp.isInChatChannel(channel)) {
                    slp.getPlayer().sendMessage(message);
                }
            } 
        }
    }
    
    public static void sendMessage(String prefix, String message, String channel) {
        ChatChannelMessageEvent ccme = new ChatChannelMessageEvent(channel, message);
        message = prefix + message;
        Bukkit.getPluginManager().callEvent(ccme);
        message = ccme.getMessage();
        channel = ccme.getChannel();
        if(!ccme.isCancelled()) {
            for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                if(slp.isInChatChannel(channel)) {
                    slp.getPlayer().sendMessage(message);
                }
            } 
        }
    }
}