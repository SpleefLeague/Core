/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.io;

import com.mongodb.BasicDBList;
import java.util.HashSet;
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
    
    public static class HashSetStringConverter extends TypeConverter<BasicDBList, HashSet<String>> {

        @Override
        public HashSet<String> convertLoad(BasicDBList t) {
            HashSet<String> hs = new HashSet<>();
            for (Object o : t) {
                hs.add((String) o);
            }
            return hs;
        }

        @Override
        public BasicDBList convertSave(HashSet<String> v) {
            BasicDBList bdbl = new BasicDBList();
            for (String s : v) {
                bdbl.add(s);
            }
            return bdbl;
        }
    }
    
    public static class HashSetIntegerConverter extends TypeConverter<BasicDBList, HashSet<Integer>> {

        @Override
        public HashSet<Integer> convertLoad(BasicDBList t) {
            HashSet<Integer> hs = new HashSet<>();
            for (Object o : t) {
                hs.add((Integer) o);
            }
            return hs;
        }

        @Override
        public BasicDBList convertSave(HashSet<Integer> v) {
            BasicDBList bdbl = new BasicDBList();
            for (Integer s : v) {
                bdbl.add(s);
            }
            return bdbl;
        }
    }
}
