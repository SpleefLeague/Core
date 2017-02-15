/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.io;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 *
 * @author Jonas
 */
public class Settings {

    private static final HashMap<String, Document> settings;

    static {
        settings = new HashMap<>();
    }

    public static void loadSettings() {
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getPluginDB().getCollection("Settings").find().iterator();
        while (dbc.hasNext()) {
            Document dbo = dbc.next();
            String key = (String) dbo.get("key");
            settings.put(key, dbo);
        }
    }

    public static boolean hasKey(String key) {
        return settings.containsKey(key);
    }

    public static Optional<Document> getDocument(String key) {
        return Optional.ofNullable(settings.get(key).get("value", Document.class));
    }

    public static Optional<String> getString(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(doc.get("value", String.class));
    }

    public static OptionalInt getInteger(String key) {
        Document doc = (Document) settings.get(key);
        if(doc == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(doc.get("value", int.class));
    }

    public static Optional<Boolean> getBoolean(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(doc.get("value", boolean.class));
    }

    public static Optional<Location> getLocation(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return null;
        }
        return Optional.of(get(key, LocationWrapper.class).get().location);
    }
    
    public static Optional<List> getList(String key) {
        Document doc = settings.get(key);
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(doc.get("value", List.class));
    }

    public static Optional<Object> get(String key) {
        Document doc = settings.get(key);
        if(doc == null) {
            return Optional.empty();
        }
        else {
            return Optional.of(doc.get("value"));
        }
    }

    public static <T extends DBEntity & DBLoadable> Optional<T> get(String key, Class<? extends T> c) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return Optional.empty();
        }
        Object value = doc.get("value");
        if (c.isAssignableFrom(value.getClass())) {
            return Optional.of((T)value);
        } else if (value instanceof Document) {
            return Optional.of(EntityBuilder.load((Document) value, c));
        } else {
            return Optional.empty();
        }
    }

    public static <T extends DBEntity & DBLoadable & DBSaveable> void set(String key, String value) {
        Document doc = new Document();
        doc.put("key", key);
        doc.put("value", value);
        save(key, doc);
    }

    public static <T extends DBEntity & DBLoadable & DBSaveable> void set(String key, boolean value) {
        Document doc = new Document();
        doc.put("key", key);
        doc.put("value", value);
        save(key, doc);
    }

    public static <T extends DBEntity & DBLoadable & DBSaveable> void set(String key, int value) {
        Document doc = new Document();
        doc.put("key", key);
        doc.put("value", value);
        save(key, doc);
    }

    public static <T extends DBEntity & DBLoadable & DBSaveable> void set(String key, T object) {
        Document value = EntityBuilder.serialize(object);
        Document doc = new Document();
        doc.put("key", key);
        doc.put("value", value);
        save(key, doc);
    }

    private static void save(String key, Document doc) {
        settings.put(key, doc);
        if (!settings.containsKey(key)) {
            Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                SpleefLeague.getInstance().getPluginDB().getCollection("Settings").insertOne(doc);
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                SpleefLeague.getInstance().getPluginDB().getCollection("Settings").replaceOne(new Document("key", key), doc);
            });
        }
    }

    public static class LocationWrapper extends DBEntity implements DBLoadable, DBSaveable {

        @DBLoad(fieldName = "location", typeConverter = TypeConverter.LocationConverter.class)
        public Location location;
    }
}
