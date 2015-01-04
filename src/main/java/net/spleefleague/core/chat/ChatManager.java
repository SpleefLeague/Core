/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.chat;

import java.util.Collection;
import java.util.HashSet;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.ChatChannelMessageEvent;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;

/**
 *
 * @author Jonas
 */
public class ChatManager {
    
    public static void sendMessage(final String m, final String c) {
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                String message = m, channel = c;
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
        });
    }
    
    public static void sendMessage(final String p, final String m, final String c) {
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                String message = m, channel = c;
                ChatChannelMessageEvent ccme = new ChatChannelMessageEvent(channel, message);
                Bukkit.getPluginManager().callEvent(ccme);
                message = p + " " + ccme.getMessage();
                channel = ccme.getChannel();
                if(!ccme.isCancelled()) {
                    for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                        if(slp.isInChatChannel(channel)) {
                            slp.getPlayer().sendMessage(message);
                        }
                    } 
                }
            }
        });
    }
    
    private static HashSet<ChatChannel> publicChannels = new HashSet<>();
    
    public static void registerPublicChannel(ChatChannel channel) {
        publicChannels.add(channel);
        if(channel.isDefault()) {
            for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                if(!slp.isInChatChannel(channel.getName())) {
                    slp.addChatChannel(channel.getName());
                }
            }
        }
    }
    
    public static Collection<ChatChannel> getAvailableChatChannels(SLPlayer slp) {
        HashSet<ChatChannel> channels = new HashSet<>();
        for(ChatChannel channel : publicChannels) {
            if(slp.getRank().hasPermission(channel.getMinRank())) {
                channels.add(channel);
            }
        }
        return channels;
    }
}