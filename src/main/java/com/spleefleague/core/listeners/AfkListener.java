/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Jonas
 */
public class AfkListener implements Listener {

    private static Listener instance;
    private static Map<UUID, Long> lastAction;
    private static BukkitTask task;
    private static final long AFK_TIME = 1000 * 60 * 10;

    public static void init() {
        if (instance == null) {
            lastAction = new HashMap<>();
            instance = new AfkListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
            task = Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
                long time = System.currentTimeMillis();
                for (SLPlayer player : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                    if (player == null) {
                        continue;
                    }
                    if (time - lastAction.get(player.getUniqueId()) > AFK_TIME &&
                            player.getState() != PlayerState.INGAME &&
                            !player.getRank().hasPermission(Rank.MODERATOR) && !player.isDonor()) {
                        player.kickPlayer(ChatColor.RED + "You have been afk for too long!");
                    }
                }
            }, 0, 20 * 10);
        }
    }

    public static void stop() {
        task.cancel();
        PlayerJoinEvent.getHandlerList().unregister(instance);
        PlayerQuitEvent.getHandlerList().unregister(instance);
        PlayerMoveEvent.getHandlerList().unregister(instance);
        PlayerCommandPreprocessEvent.getHandlerList().unregister(instance);
    }

    private AfkListener() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            lastAction.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        lastAction.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastAction.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        lastAction.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        lastAction.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}
