/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.listeners.ChatListener;
import com.spleefleague.core.player.SLPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jonas
 */
public class ChatManager {

    public static void sendMessagePlayer(SLPlayer to, String message) {
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
            to.sendMessage(message);
        });
    }

    public static void sendMessage(String p, String m, ChatChannel c) {
        sendMessage(p.concat(" ").concat(m), c);
    }

    public static void sendMessage(final String m, final ChatChannel c) {
        if (c == ChatChannel.STAFF) {
            JSONObject send = new JSONObject();
            send.put("message", m);
            SpleefLeague.getInstance().getConnectionClient().send("staff", send);
        } else {
            Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                Bukkit.getConsoleSender().sendMessage(m);
                SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (slp.isInChatChannel(c))).forEach((slp) -> {
                    slp.sendMessage(m);
                });
            });
        }
    }

    public static void sendMessage(final BaseComponent[] m, final ChatChannel c) {
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
            SpleefLeague.getInstance().getPlayerManager().getAll().stream().filter((slp) -> (slp.isInChatChannel(c))).forEach((slp) -> {
                slp.spigot().sendMessage(m);
            });
        });
    }

    public static void sendMessage(ChatChannel c, BaseComponent... baseComponents) {
        sendMessage(baseComponents, c);
    }

    private static final HashSet<ChatChannel> channels = new HashSet<>();

    public static void registerChannel(ChatChannel channel) {
        registerChannel(channel, false);
    }

    public static void registerChannel(ChatChannel channel, boolean silent) {
        channels.add(channel);
        if (!silent && channel.isDefault()) {
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
        Set<ChatChannel> availableChannels = new HashSet<>();
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
