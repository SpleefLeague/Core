/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.GeneralPlayerLoadedEvent;
import net.spleefleague.core.utils.DatabaseConnection;
import net.spleefleague.core.utils.EntityBuilder;
import org.bson.types.ObjectId;
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
            DBObject dbo = db.getCollection("Players").findOne(new BasicDBObject("uuid", player.getUniqueId().toString()));
            G generalPlayer;
            if(dbo == null) {
                generalPlayer = c.newInstance();
                generalPlayer.setName(player.getName());
                generalPlayer.setUUID(player.getUniqueId());
                generalPlayer.setDefaults();
                ObjectId _id = EntityBuilder.save(generalPlayer, db.getCollection("Players"), null);
                generalPlayer.setObjectId(_id);
            }
            else {
                generalPlayer = EntityBuilder.load(dbo, c);
            }
            map.put(player, generalPlayer);
            callEvent(generalPlayer, dbo == null);    
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
                DatabaseConnection.updateCache(event.getPlayer().getUniqueId(), event.getPlayer().getName());
            }
        });
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final G gp = get(event.getPlayer());
        this.map.remove(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                EntityBuilder.save(gp, db.getCollection("Players"), new BasicDBObject("_id", gp.getObjectId()));
            }
        });
    }
    
    private void callEvent(GeneralPlayer generalPlayer, boolean firstJoin) {
        Event e = new GeneralPlayerLoadedEvent(generalPlayer, firstJoin);
        Bukkit.getPluginManager().callEvent(e);
    }
}
