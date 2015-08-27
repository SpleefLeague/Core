package com.spleefleague.core.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bukkit.Location;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBLoadable;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.DBSaveable;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.TypeConverter;

public class Warp extends DBEntity implements DBLoadable, DBSaveable{
	
	@DBLoad(fieldName = "name")
	@DBSave(fieldName = "name")
	private String name;
	
	@DBLoad(fieldName = "location", typeConverter = TypeConverter.LocationConverter.class)
	@DBSave(fieldName = "location", typeConverter = TypeConverter.LocationConverter.class)
	private Location location;

	public Warp(){
		
	}
	
	public Warp(String name,Location location){
		this.name = name;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}
	
	private static Map<String, Warp> warps;
	
	public static Warp byName(String name){
		return warps.get(name);
	}
	
	public static Collection<Warp> getAll(){
		return warps.values();
	}
	
	public static void initialize(){
		warps = new HashMap<>();
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getPluginDB().getCollection("Warps").find().iterator();
        while(dbc.hasNext()) {
            Warp warp = EntityBuilder.load(dbc.next(), Warp.class);
            warps.put(warp.getName(), warp);
        }
        SpleefLeague.getInstance().log("Loaded " + warps.size() + " warps!");
    }
	
	public static void addWarp(Warp warp){
		warps.put(warp.getName(), warp);
		MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Warps");
		EntityBuilder.save(warp, collection);
	}
	
	public static void removeWarp(Warp warp){
		warps.remove(warp.getName());
		MongoCollection<Document> coll = SpleefLeague.getInstance().getPluginDB().getCollection("Warps");
		EntityBuilder.delete(warp, coll);
	}
}
