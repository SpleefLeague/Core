/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import com.mongodb.DB;
import java.lang.reflect.ParameterizedType;
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
    
    public PlayerManager(DB db) {
        this.map = new ConcurrentHashMap<>();
        this.db = db;
        for(Player player : Bukkit.getOnlinePlayers()) {
            load(player, getPlayerClass());
        }
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
    }
    
    public Class getPlayerClass() {
        return (Class)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    public G get(Player player) {
        return map.get(player);
    }
    
    public void add(Player player, G gp) {
        map.put(player, gp);
    }
    
    private void load(Player player, Class<G> c) {
        try {
            G generalPlayer = c.newInstance();
            generalPlayer.setUUID(player.getUniqueId());
            generalPlayer.setUsername(player.getName());
            generalPlayer.load(db);
            callEvent(player, generalPlayer);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.load(event.getPlayer(), getPlayerClass());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.map.remove(event.getPlayer());
    }
    
    private void callEvent(Player player, GeneralPlayer generalPlayer) {
        Event e = new PlayerLoadedEvent(player, generalPlayer);
        Bukkit.getPluginManager().callEvent(e);
    }
}
