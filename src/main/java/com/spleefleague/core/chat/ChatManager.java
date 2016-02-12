/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat;

import java.util.Collection;
import java.util.HashSet;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.listeners.ChatListener;
import com.spleefleague.core.player.SLPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;

/**
 *
 * @author Jonas
 */
public class ChatManager {
    
    public static void sendMessage(String p, String m, ChatChannel c) {
        sendMessage(p.concat(" ").concat(m), c);
    }
    
    public static void sendMessage(final String m, final ChatChannel c) {
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
            Bukkit.getConsoleSender().sendMessage(m);
            SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (slp.isInChatChannel(c))).forEach((slp) -> {
                slp.sendMessage(m);
            });
        });
    }

    public static void sendMessage(final BaseComponent[] m, final ChatChannel c) {
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
            SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (slp.isInChatChannel(c))).forEach((slp) -> {
                slp.spigot().sendMessage(m);
            });
        });
    }

    public static void sendMessage(ChatChannel c, BaseComponent... baseComponents) {
        sendMessage(c, baseComponents);
    }

    private static final HashSet<ChatChannel> channels = new HashSet<>();
    
    public static void registerChannel(ChatChannel channel) {
        registerChannel(channel, false);
    }
    
    public static void registerChannel(ChatChannel channel, boolean silent) {
        channels.add(channel);
        if(!silent && channel.isDefault()) {
            SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (!slp.isInChatChannel(channel) && slp.getOptions().isChatChannelEnabled(channel))).forEach((slp) -> {
                slp.addChatChannel(channel);
            });
        }
    }
    
    public static void unregisterChannel(ChatChannel channel) {
        channels.remove(channel);
        SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (slp.isInChatChannel(channel))).forEach((slp) -> {
            slp.removeChatChannel(channel);
        });
    }
    
    public static Collection<ChatChannel> getAvailableChatChannels(SLPlayer slp) {
        HashSet<ChatChannel> availableChannels = new HashSet<>();
        channels.stream().filter((channel) -> (channel.isVisible() && slp.getRank().hasPermission(channel.getMinRank()))).forEach((channel) -> {
            availableChannels.add(channel);
        });
        return availableChannels;
    }
    
    public static Collection<ChatChannel> getAllChatChannels() {
        return channels;
    }
    
    public static Collection<ChatChannel> getVisibleChatChannels() {
        List<ChatChannel> availableChannels = new ArrayList<>();
        channels.stream().sorted().filter((channel) -> (channel.isVisible())).forEach((channel) -> {
            availableChannels.add(channel);
        });
        return availableChannels;
    }

    public static void init() {
        ChatListener.init();
        ChatChannel.init();
    }
}