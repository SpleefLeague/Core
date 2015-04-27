/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.io;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.HashMap;
import net.spleefleague.core.SpleefLeague;
import org.bukkit.Location;

/**
 *
 * @author Jonas
 */
public class Settings {
    
    private static final HashMap<String, DBObject> settings;
    
    static {
        settings = new HashMap<>();
    }

    public static void loadSettings() {
        DBCursor dbc = SpleefLeague.getInstance().getMongo().getDB("SpleefLeague").getCollection("Settings").find();
        while(dbc.hasNext()) {
            DBObject dbo = dbc.next();
            String key = (String)dbo.get("key");
            settings.put(key, dbo);
        }
    }

    public static boolean hasKey(String key) {
        return settings.containsKey(key);
    }

    public static String getString(String key) {
        return (String)settings.get(key).get("value");
    }

    public static int getInteger(String key) {
        return (int)settings.get(key).get("value");
    }

    public static boolean getBoolean(String key) {
        return (boolean)settings.get(key).get("value");
    }
    
    public static Location getLocation(String key) {
        return get(key, LocationWrapper.class).location;
    }
    
    public static <T extends DBEntity & DBLoadable> T get(String key, Class<? extends T> c) {
        return EntityBuilder.load((DBObject)settings.get(key), c);
    }
    
    public static class LocationWrapper extends DBEntity implements DBLoadable {
        @DBLoad(fieldName = "value", typeConverter = TypeConverter.LocationConverter.class)
        public Location location;
    }
}