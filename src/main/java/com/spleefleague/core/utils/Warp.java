package com.spleefleague.core.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bukkit.Location;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.typeconverters.LocationConverter;
import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBLoad;
import com.spleefleague.entitybuilder.DBLoadable;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;
import com.spleefleague.entitybuilder.EntityBuilder;
import org.bukkit.Bukkit;

public class Warp extends DBEntity implements DBLoadable, DBSaveable, Comparable<Warp> {

    @DBLoad(fieldName = "name")
    @DBSave(fieldName = "name")
    private String name;

    @DBLoad(fieldName = "location", typeConverter = LocationConverter.class)
    @DBSave(fieldName = "location", typeConverter = LocationConverter.class)
    private Location location;

    public Warp(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int compareTo(Warp w) {
        return name.compareTo(w.name);
    }

    private static Map<String, Warp> warps;

    public static Warp byName(String name) {
        return warps.get(name.toLowerCase());
    }

    public static Collection<Warp> getAll() {
        return warps.values();
    }

    public static void init() {
        warps = new HashMap<>();
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getPluginDB().getCollection("Warps").find().iterator();
        while (dbc.hasNext()) {
            Warp warp = EntityBuilder.load(dbc.next(), Warp.class);
            warps.put(warp.getName().toLowerCase(), warp);
        }
        SpleefLeague.getInstance().log("Loaded " + warps.size() + " warps!");
    }

    public static void addWarp(Warp warp) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            warps.put(warp.getName().toLowerCase(), warp);
            MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Warps");
            EntityBuilder.save(warp, collection);
        });
    }

    public static void removeWarp(Warp warp) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            warps.remove(warp.getName().toLowerCase());
            MongoCollection<Document> coll = SpleefLeague.getInstance().getPluginDB().getCollection("Warps");
            EntityBuilder.delete(warp, coll);
        });
    }
}
