/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import java.util.UUID;
import net.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Jonas
 * @param <T>
 * @param <V>
 */
public abstract class TypeConverter<T, V> {

    public abstract V convertLoad(T t);

    public abstract T convertSave(V v);

    //Some common TypeConverters
    public static class UUIDStringConverter extends TypeConverter<String, UUID> {

        @Override
        public String convertSave(UUID t) {
            return t.toString();
        }

        @Override
        public UUID convertLoad(String v) {
            return UUID.fromString(v);
        }
    }

    public static class LocationConverter extends TypeConverter<BasicDBList, Location> {

        @Override
        public Location convertLoad(BasicDBList t) {
            double x, y, z;
            World world;
            if(t.get(0) instanceof Integer) {
                x = (Integer)t.get(0);
            }
            else {
                x = (double)t.get(0);
            }
            if(t.get(1) instanceof Integer) {
                y = (Integer)t.get(1);
            }
            else {
                y = (double)t.get(1);
            }
            if(t.get(2) instanceof Integer) {
                z = (Integer)t.get(2);
            }
            else {
                z = (double)t.get(2);
            }
            world = (t.size() == 4) ? Bukkit.getWorld((String) t.get(3)) : SpleefLeague.DEFAULT_WORLD;
            return new Location(world, x, y, z);
        }

        @Override
        public BasicDBList convertSave(Location v) {
            BasicDBList bdbl = new BasicDBList();
            bdbl.add(v.getX());
            bdbl.add(v.getY());
            bdbl.add(v.getZ());
            if (v.getWorld() != SpleefLeague.DEFAULT_WORLD) {
                bdbl.add(v.getWorld().getName());
            }
            return bdbl;
        }
    }
    
    public static class LocationArrayConverter extends TypeConverter<BasicDBList, Location[]> {

        private static final LocationConverter lc = new LocationConverter();
        
        @Override
        public Location[] convertLoad(BasicDBList t) {
            Location[] array = new Location[t.size()];
            for(int i = 0; i < array.length; i++) {
                BasicDBList loc = (BasicDBList)t.get(i);
                array[i] = lc.convertLoad(loc);
            }
            return array;
        }

        @Override
        public BasicDBList convertSave(Location[] v) {
            BasicDBList bdbl = new BasicDBList();
            for(Location loc : v) {
                bdbl.add(lc.convertSave(loc));
            }
            return bdbl;
        }
    }
}
