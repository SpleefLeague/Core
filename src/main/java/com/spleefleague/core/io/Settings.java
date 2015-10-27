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
        while(dbc.hasNext()) {
            Document dbo = dbc.next();
            String key = (String)dbo.get("key");
            settings.put(key, dbo);
        }
    }

    public static boolean hasKey(String key) {
        return settings.containsKey(key);
    }

    public static String getString(String key) {
        Document doc = (Document)settings.get(key);
        if(doc == null) return null;
        return (String)settings.get(key).get("value");
    }

    public static int getInteger(String key) {
        Document doc = (Document)settings.get(key);
        if(doc == null) return 0;
        return (int)settings.get(key).get("value");
    }

    public static boolean getBoolean(String key) {
        Document doc = (Document)settings.get(key);
        if(doc == null) return true;
        return (boolean)settings.get(key).get("value");
    }
    
    public static Location getLocation(String key) {
        Document doc = (Document)settings.get(key);
        if(doc == null) return null;
        return get(key, LocationWrapper.class).location;
    }
    
    public static List getList(String key) {
        Document doc = (Document)settings.get(key);
        if(doc == null) return null;
        return (List)settings.get(key).get("value");
    }
    
    public static <T extends DBEntity & DBLoadable> T get(String key, Class<? extends T> c) {
        Document doc = (Document)settings.get(key);
        if(doc == null) return null;
        return EntityBuilder.load(doc, c);
    }
    
    public static class LocationWrapper extends DBEntity implements DBLoadable {
        @DBLoad(fieldName = "value", typeConverter = TypeConverter.LocationConverter.class)
        public Location location;
    }
}