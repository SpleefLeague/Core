/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.GamePlugin;
import com.spleefleague.core.queue.Battle;
import com.spleefleague.core.utils.ModifiableFinal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer().getUniqueId());
        if (!lastMessage.containsKey(slp.getUniqueId()) || System.currentTimeMillis() - lastMessage.get(slp.getUniqueId()) > 3000) {
            ModifiableFinal<String> prefix = new ModifiableFinal<>("");
            if (!slp.getRank().getDisplayName().equals("Default")) {
                prefix.setValue(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + slp.getRank().getDisplayName() + ChatColor.DARK_GRAY + "] ");
            }
            if (!event.isCancelled()) {
                ModifiableFinal<Battle> b = new ModifiableFinal<>(null);
                for (GamePlugin p : GamePlugin.getGamePlugins()) {
                    Battle find = p.getBattleManager().getBattle(slp);
                    if (find != null) {
                        b.setValue(find);
                        break;
                    }
                }
                ChatChannel channel = slp.getSendingChannel();
                ChatManager.sendMessage(ChatColor.DARK_GRAY + "<" + prefix.getValue() + slp.getRank().getColor() + slp.getName() + ChatColor.DARK_GRAY + ">" + ChatColor.RESET, event.getMessage(), channel);
            }
        }
        if (slp.getRank() != null && !(slp.getRank().hasPermission(Rank.MODERATOR) || Arrays.asList(Rank.MODERATOR).contains(slp.getRank()))) {
            lastMessage.put(slp.getUniqueId(), System.currentTimeMillis());
        }
        event.setCancelled(true);
    }
}
