/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import com.mongodb.client.MongoDatabase;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.GeneralPlayerLoadedEvent;
import net.spleefleague.core.io.EntityBuilder;
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
    
    private final ConcurrentHashMap<Player, G> map;
    private final MongoDatabase db;
    private final Class<G> playerClass;
    
    public PlayerManager(CorePlugin plugin, Class<G> playerClass) {
        this.map = new ConcurrentHashMap<>();
        this.db = plugin.getPluginDB();
        this.playerClass = playerClass;
        for(Player player : Bukkit.getOnlinePlayers()) {
            load(player, getPlayerClass());
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
    
    private void load(final Player player, final Class<G> c) {
            final Document doc = db.getCollection("Players").find(new Document("uuid", player.getUniqueId().toString())).first();
            Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), new Runnable() {
                @Override
                public void run() {
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
                        map.put(player, generalPlayer);
                        callEvent(generalPlayer, doc == null);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });   
    }
    
    public void saveAll() {
        for (GeneralPlayer gp : getAll()) {
            EntityBuilder.save(gp, db.getCollection("Players"));
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
    
    @EventHandler(priority = EventPriority.MONITOR) //Misleading, has to be called last
    public void onQuit(PlayerQuitEvent event) {
        final G gp = get(event.getPlayer());
        this.map.remove(event.getPlayer());
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                EntityBuilder.save(gp, db.getCollection("Players"));
            }
        });
    }
    
    private void callEvent(GeneralPlayer generalPlayer, boolean firstJoin) {
        Event e = new GeneralPlayerLoadedEvent(generalPlayer, firstJoin);
        Bukkit.getPluginManager().callEvent(e);
    }
}