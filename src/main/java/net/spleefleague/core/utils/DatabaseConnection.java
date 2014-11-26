/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.UUID;
import net.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;

/**
 *
 * @author Jonas
 */
public class DatabaseConnection {

    private static final UUIDCache uuidCache = new UUIDCache(1000);
    
    public static void updateCache(UUID uuid, String username) {
        uuidCache.insert(uuid, username);
    }

    public static UUID getUUID(String username) {
        UUID uuid = uuidCache.getUUID(username);
        if (uuid != null) {
            return uuid;
        }
        DBObject dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").findOne(new BasicDBObject("username", username));
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
        DBObject dbo = SpleefLeague.getInstance().getPluginDB().getCollection("Players").findOne(new BasicDBObject("uuid", uuid.toString()));
        if (dbo != null) {
            username = (String) dbo.get("username");
            updateCache(uuid, username);
            return username;
        } else {
            return null;
        }
    }

    public static void updateFields(final DBCollection dbcoll, final DBObject index, final DBObject update) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), new Runnable() {
            @Override
            public void run() {
                dbcoll.update(index, new BasicDBObject("$set", update));
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