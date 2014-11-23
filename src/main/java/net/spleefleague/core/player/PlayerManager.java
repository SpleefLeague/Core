/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import com.mongodb.DB;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Jonas
 * @param <G>
 */
public class PlayerManager<G extends GeneralPlayer> implements Listener {
    
    private final ConcurrentHashMap<Player, G> map;
    private final DB db;
    private final Class<G> playerClass;
    
    public PlayerManager(DB db, Class<G> playerClass) {
        this.map = new ConcurrentHashMap<>();
        this.db = db;
        this.playerClass = playerClass;
        for(Player player : Bukkit.getOnlinePlayers()) {
            load(player, getPlayerClass());
        }
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
    }
    
    public Class<G> getPlayerClass() {
        return playerClass;
    }
    
    public G get(Player player) {
        return map.get(player);
    }
    
    public Collection<G> getAll() {
        return map.values();
    }
    
    private void load(Player player, Class<G> c) {
        try {
            G generalPlayer = c.newInstance();
            generalPlayer.setDB(db);
            generalPlayer.setUUID(player.getUniqueId());
            generalPlayer.setName(player.getName());
            generalPlayer.load(db);
            map.put(player, generalPlayer);
            callEvent(player, generalPlayer);
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                load(event.getPlayer(), getPlayerClass());
            }
        });
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final G slp = get(event.getPlayer());
        this.map.remove(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                slp.save();
            }
        });
    }
    
    private void callEvent(Player player, GeneralPlayer generalPlayer) {
        Event e = new PlayerLoadedEvent(player, generalPlayer);
        Bukkit.getPluginManager().callEvent(e);
    }
}
