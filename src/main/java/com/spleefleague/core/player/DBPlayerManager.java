/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.plugin.PlayerHandling;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.entitybuilder.EntityBuilder;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas
 * @param <G>
 */
public class DBPlayerManager<G extends GeneralPlayer> extends PlayerManager<G> {

    private final MongoDatabase db;

    public <C extends CorePlugin & PlayerHandling> DBPlayerManager(C plugin, Class<G> playerClass) {
        super(plugin, playerClass);
        this.db = plugin.getPluginDB();
        Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
            List<G> ghostPlayers = new ArrayList<>();
            map.values().stream().filter((G g) -> Bukkit.getPlayer(g.getUniqueId()) == null).forEach(ghostPlayers::add);
            if (ghostPlayers.isEmpty()) {
                return;
            }
            SpleefLeague.getInstance().getLogger().severe("[!!] Ghost instances found!");
            SpleefLeague.getInstance().getLogger().severe("[!!] Names:");
            ghostPlayers.forEach((G g) -> SpleefLeague.getInstance().getLogger().log(Level.SEVERE, "[!!] {0} (created at MS: {1}).", new Object[]{g.getName(), g.getCreated()}));
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
        
    }
    
    public G loadFake(Document query) {
        return load(query);
    }
    
    public G loadFake(Document query, Collation collation) {
        return load(query, collation);
    }

    public G loadFake(String name) {
        return load(new Document("username", name), DatabaseConnection.usernameCollation);
    }

    public G loadFake(UUID uuid) {
        return load(new Document("uuid", uuid.toString()));
    }

    private G load(Document query, Collation collation) {
        Document doc = db.getCollection("Players").find(query).collation(collation).first();
        if (doc == null) {
            return null;
        } else {
            return EntityBuilder.load(doc, getPlayerClass());
        }
    }

    private G load(Document query) {
        return load(query, null);
    }
    
    @Override
    protected void load(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            DatabaseConnection.find(db.getCollection("Players"), new Document("uuid", player.getUniqueId().toString()), (result) -> {
                Document doc = result.first();
                Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
                    try {
                        G generalPlayer = null;
                        boolean broken = false;
                        if (doc != null) {
                            try {
                                generalPlayer = EntityBuilder.load(doc, getPlayerClass());
                                generalPlayer.setName(player.getName());
                            } catch(Exception e) {
                                broken = true;
                                e.printStackTrace();
                            }
                        }
                        if (broken || doc == null) {
                            generalPlayer = getPlayerClass().newInstance();
                            generalPlayer.setName(player.getName());
                            generalPlayer.setUUID(player.getUniqueId());
                            generalPlayer.setDefaults();
                            if(doc != null) {
                                generalPlayer.saveProfile(doc);
                                generalPlayer.setObjectId(doc.getObjectId("_id"));
                            }
                            G toSave = generalPlayer;
                            Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                                EntityBuilder.save(toSave, db.getCollection("Players"));
                            });
                        }
                        if(generalPlayer != null) {
                            map.put(player.getUniqueId(), generalPlayer);
                            callEvent(generalPlayer, doc == null);
                        }
                        else {
                            player.kickPlayer("Error loading player data!");
                        }
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(DBPlayerManager.class.getName()).log(Level.SEVERE, null, ex);
                        player.kickPlayer("Error loading player data!");
                    }
                });
            });
        });
    }

    public void saveAll() {
        getAll().stream().forEach((gp) -> {
            save(gp);
        });
    }

    @Override
    public void save(G g) {
        EntityBuilder.save(g, db.getCollection("Players"));
    }

    private void callEvent(GeneralPlayer generalPlayer, boolean firstJoin) {
        Event e = new GeneralPlayerLoadedEvent(generalPlayer, firstJoin);
        Bukkit.getPluginManager().callEvent(e);
    }
}
