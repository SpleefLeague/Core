/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.DatabaseConnection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
            List<G> ghostPlayers = new ArrayList<>();
            map.values().stream().filter((G g) -> Bukkit.getPlayer(g.getUniqueId()) == null).forEach(ghostPlayers::add);
            if (ghostPlayers.isEmpty()) {
                return;
            }
            SpleefLeague.getInstance().getLogger().severe("[!!] Ghost instances found!");
            SpleefLeague.getInstance().getLogger().severe("[!!] Names:");
            ghostPlayers.forEach((G g) -> SpleefLeague.getInstance().getLogger().severe("[!!] " + g.getName() + " (created at MS: " + g.getCreated() + ")."));
            SpleefLeague.getInstance().getLogger().severe("[!!] Removing them now.");
            ghostPlayers.forEach((G g) -> {
                try {
                    this.map.remove(g.getUniqueId());
                } catch (Exception e) {
                    this.map.values().remove(g);
                }
            });
        }, 1200, 1200);

        Bukkit.getOnlinePlayers().stream().forEach((player) -> {
            load(player);
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

    public G loadFake(String name) {
        return load(new Document("username", name));
    }

    public G loadFake(UUID uuid) {
        return load(new Document("uuid", uuid.toString()));
    }

    private G load(Document query) {
        Document doc = db.getCollection("Players").find(query).first();
        if (doc == null) {
            return null;
        } else {
            return EntityBuilder.load(doc, getPlayerClass());
        }
    }

    private void load(final Player player) {
        DatabaseConnection.find(db.getCollection("Players"), new Document("uuid", player.getUniqueId().toString()), (result) -> {
            Document doc = result.first();
            Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                try {
                    G generalPlayer;
                    if (doc == null) {
                        generalPlayer = getPlayerClass().newInstance();
                        generalPlayer.setName(player.getName());
                        generalPlayer.setUUID(player.getUniqueId());
                        generalPlayer.setDefaults();
                        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                            EntityBuilder.save(generalPlayer, db.getCollection("Players"));
                        });
                    } else {
                        generalPlayer = EntityBuilder.load(doc, getPlayerClass());
                        generalPlayer.setName(player.getName());
                    }
                    map.put(player.getUniqueId(), generalPlayer);
                    callEvent(generalPlayer, doc == null);

                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(PlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
    }

    public void saveAll() {
        getAll().stream().forEach((gp) -> {
            save(gp);
        });
    }

    public void save(G g) {
        EntityBuilder.save(g, db.getCollection("Players"));
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> load(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR) //Misleading, has to be called last
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

    private void callEvent(GeneralPlayer generalPlayer, boolean firstJoin) {
        Event e = new GeneralPlayerLoadedEvent(generalPlayer, firstJoin);
        Bukkit.getPluginManager().callEvent(e);
    }
}
