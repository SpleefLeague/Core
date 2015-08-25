/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.mongodb.client.MongoDatabase;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.io.EntityBuilder;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Jonas
 * @param <G>
 */
public class PlayerManager<G extends GeneralPlayer> implements Listener {
    
    private final ConcurrentHashMap<UUID, G> map;
    private final MongoDatabase db;
    private final Class<G> playerClass;
    
    public PlayerManager(CorePlugin plugin, Class<G> playerClass) {
        this.map = new ConcurrentHashMap<>();
        this.db = plugin.getPluginDB();
        this.playerClass = playerClass;
        Bukkit.getOnlinePlayers().stream().forEach((player) -> {
            load(player, getPlayerClass());
        });
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
    
    private void load(final Player player, final Class<G> c) {
            final Document doc = db.getCollection("Players").find(new Document("uuid", player.getUniqueId().toString())).first();
            Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                try {
                    G generalPlayer;
                    if (doc == null) {
                        generalPlayer = c.newInstance();
                        generalPlayer.setName(player.getName());
                        generalPlayer.setUUID(player.getUniqueId());
                        generalPlayer.setDefaults();
                        EntityBuilder.save(generalPlayer, db.getCollection("Players"));
                    }
                    else {
                        generalPlayer = EntityBuilder.load(doc, c);
                        generalPlayer.setName(player.getName());
                    }
                    map.put(player.getUniqueId(), generalPlayer);
                    callEvent(generalPlayer, doc == null);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });   
    }
    
    public void saveAll() {
        getAll().stream().forEach((gp) -> {
            EntityBuilder.save(gp, db.getCollection("Players"));
        });
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            load(event.getPlayer(), getPlayerClass());
        });
    }
    
    @EventHandler(priority = EventPriority.MONITOR) //Misleading, has to be called last
    public void onQuit(PlayerQuitEvent event) {
        final G gp = get(event.getPlayer());
        this.map.remove(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            EntityBuilder.save(gp, db.getCollection("Players"));
        });
    }
    
    private void callEvent(GeneralPlayer generalPlayer, boolean firstJoin) {
        Event e = new GeneralPlayerLoadedEvent(generalPlayer, firstJoin);
        Bukkit.getPluginManager().callEvent(e);
    }
}