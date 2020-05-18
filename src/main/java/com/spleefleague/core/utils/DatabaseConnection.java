/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.collections.FixedSizeList;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Jonas
 */
public class DatabaseConnection {

    private static final UUIDCache uuidCache = new UUIDCache(100);
    private static final RankCache rankCache = new RankCache(100);
    public static Collation usernameCollation;

    public static void initialize() {
        usernameCollation = Collation.builder()
                .locale("en")
                .collationStrength(CollationStrength.SECONDARY)
                .build();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(GeneralPlayerLoadedEvent event) {
                if (event.getPlayer() == null) {
                    return;
                }
                DatabaseConnection.updateCache(event.getPlayer().getUniqueId(), event.getPlayer().getName());
                if (event.getGeneralPlayer() instanceof SLPlayer) {
                    SLPlayer slp = (SLPlayer) event.getGeneralPlayer();
                    slp.setRank(slp.getRank());
                    DatabaseConnection.updateCache(event.getPlayer().getUniqueId(), slp.getRank());
                }
            }
        }, SpleefLeague.getInstance());
    }

    public static void updateCache(UUID uuid, String username) {
        uuidCache.insert(uuid, username);
    }

    public static void updateCache(UUID uuid, Rank rank) {
        rankCache.insert(uuid, rank);
    }

    public static UUID getUUID(String username) {
        UUID uuid = uuidCache.getUUID(username);
        if (uuid != null) {
            return uuid;
        }
        Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").find(new Document("username", username)).collation(usernameCollation).first();
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

    public static Rank getRank(UUID uuid) {
        Rank rank = rankCache.getRank(uuid);
        if (rank != null) {
            return rank;
        }
        Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").find(new Document("uuid", uuid.toString())).first();
        if (dbo != null) {
            rank = Rank.valueOf((String) dbo.get("rank"));
            updateCache(uuid, rank);
            return rank;
        } else {
            return null;
        }
    }
    
    public static void updateFields(MongoCollection<Document> dbcoll, Document index, Pair<String, Object>... update) {
        Map<String, Object> updates = new HashMap<>();
        for(Pair<String, Object> pair : update)
            updates.put(pair.getKey(), pair.getValue());
        updateFields(dbcoll, index, new Document(updates));
    }

    public static void updateFields(final MongoCollection<Document> dbcoll, final Document index, final Document update) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            dbcoll.updateOne(index, new Document("$set", update));
        });
    }

    public static void find(final MongoCollection<Document> dbcoll, final Document query, Consumer<FindIterable<Document>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            callback.accept(dbcoll.find(query));
        });
    }

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

    private static class RankCache {

        private final FixedSizeList<RankMapEntry> list;

        public RankCache(int size) {
            list = new FixedSizeList(size);
        }

        public void insert(UUID uuid, Rank rank) {
            for (RankMapEntry e : list) {
                if (e.rank == rank || e.uuid.equals(uuid)) {
                    e.rank = rank;
                    e.uuid = uuid;
                    list.call(e);
                    return;
                }
            }
            list.add(new RankMapEntry(uuid, rank));
        }

        public UUID getUUID(Rank rank) {
            for (RankMapEntry e : list) {
                if (e.rank == rank) {
                    list.call(e);
                    return e.uuid;
                }
            }
            return null;
        }

        public Rank getRank(UUID uuid) {
            for (RankMapEntry e : list) {
                if (e.uuid.equals(uuid)) {
                    list.call(e);
                    return e.rank;
                }
            }
            return null;
        }

        private static class RankMapEntry {

            private Rank rank;
            private UUID uuid;

            public RankMapEntry(UUID uuid, Rank rank) {
                this.uuid = uuid;
                this.rank = rank;
            }
        }
    }
}
