/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.io;

import com.mongodb.client.MongoCursor;
import java.util.HashMap;
import java.util.List;
import com.spleefleague.core.SpleefLeague;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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

    public static Document getDocument(String key) {
        Document doc = settings.get(key);
        if (doc == null) {
            return null;
        }
        return doc.get("value", Document.class);
    }

    public static String getString(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return null;
        }
        return doc.get("value", String.class);
    }

    public static int getInteger(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return 0;
        }
        return doc.get("value", int.class);
    }

    public static boolean getBoolean(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return true;
        }
        return doc.get("value", boolean.class);
    }

    public static Location getLocation(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return null;
        }
        return get(key, LocationWrapper.class).location;
    }

    public static Location getLocation(List list) {
        return new TypeConverter.LocationConverter().convertLoad(list);
    }

    public static List getList(String key) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return null;
        }
        return doc.get("value", List.class);
    }

    public static Object get(String key) {
        return settings.get(key).get("value");
    }

    public static <T extends DBEntity & DBLoadable> T get(String key, Class<? extends T> c) {
        Document doc = (Document) settings.get(key);
        if (doc == null) {
            return null;
        }
        Object value = doc.get("value");
        if (c.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (value instanceof Document) {
            return EntityBuilder.load((Document) value, c);
        } else {
            return null;
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
