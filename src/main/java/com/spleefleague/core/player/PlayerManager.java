/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.plugin.CorePlugin;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author jonas
 * @param <G>
 */
public abstract class PlayerManager<G extends GeneralPlayer> implements Listener {
    
    protected final ConcurrentHashMap<UUID, G> map;
    protected final Class<G> playerClass;
    
    public PlayerManager(CorePlugin plugin, Class<G> playerClass) {
        this.playerClass = playerClass;
        this.map = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public Class<G> getPlayerClass() {
        return playerClass;
    }

    public G get(String username) {
        Player p = Bukkit.getPlayer(username);
        return p != null ? get(p) : null;
    }

    public G get(Player player) {
        return player != null ? get(player.getUniqueId()) : null;
    }

    public G get(UUID uuid) {
        return map.get(uuid);
    }

    public Collection<G> getAll() {
        return map.values();
    }
    
    

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> load(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        final G gp = get(event.getPlayer());
        try {
            this.map.remove(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            this.map.values().remove(gp);
        }
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            save(gp);
        });
    }
    
    protected abstract void load(Player player);
    protected abstract void save(G player);
}
