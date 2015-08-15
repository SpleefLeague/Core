/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.spleefleague.core.utils.collections.FixedSizeList;
import com.mongodb.client.MongoCollection;
import java.util.UUID;
import com.spleefleague.core.SpleefLeague;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Jonas
 */
public class DatabaseConnection {

    private static final UUIDCache uuidCache = new UUIDCache(1000);
    
    public static void initialize() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                DatabaseConnection.updateCache(event.getPlayer().getUniqueId(), event.getPlayer().getName());
            }
        },SpleefLeague.getInstance());
    }
    
    public static void updateCache(UUID uuid, String username) {
        uuidCache.insert(uuid, username);
    }

    public static UUID getUUID(String username) {
        UUID uuid = uuidCache.getUUID(username);
        if (uuid != null) {
            return uuid;
        }
        Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").find(new Document("username", username)).first();
        if (dbo != null) {
            uuid = UUID.fromString((String) dbo.get("uuid"));
            updateCache(uuid, username);
            return uuid;
        } else {
            return null;
        }
    }

    public static String getUsername(UUID uuid) {
        String username = uuidCache.getUsername(uuid);
        if (username != null) {
            return username;
        }
        Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").find(new Document("uuid", uuid.toString())).first();
        if (dbo != null) {
            username = (String) dbo.get("username");
            updateCache(uuid, username);
            return username;
        } else {
            return null;
        }
    }

    public static void updateFields(final MongoCollection<Document> dbcoll, final Document index, final Document update) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                dbcoll.updateOne(index, new Document("$set", update));
            }
        });
    }

    //Private classes
    private static class UUIDCache {

        private final FixedSizeList<UUIDMapEntry> list;

        public UUIDCache(int size) {
            list = new FixedSizeList(size);
        }

        public void insert(UUID uuid, String username) {
            for (UUIDMapEntry e : list) {
                if (e.username.equals(username) || e.uuid.equals(uuid)) {
                    e.username = username;
                    e.uuid = uuid;
                    list.call(e);
                    return;
                }
            }
            list.add(new UUIDMapEntry(uuid, username));
        }

        public UUID getUUID(String username) {
            for (UUIDMapEntry e : list) {
                if (e.username.equals(username)) {
                    list.call(e);
                    return e.uuid;
                }
            }
            return null;
        }

        public String getUsername(UUID uuid) {
            for (UUIDMapEntry e : list) {
                if (e.uuid.equals(uuid)) {
                    list.call(e);
                    return e.username;
                }
            }
            return null;
        }

        private static class UUIDMapEntry {

            private UUID uuid;
            private String username;

            public UUIDMapEntry(UUID uuid, String username) {
                this.uuid = uuid;
                this.username = username;
            }
        }
    }
}