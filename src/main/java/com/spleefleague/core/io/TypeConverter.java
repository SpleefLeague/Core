/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.io;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.Rank;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static class RankStringConverter extends TypeConverter<String, Rank> {

        @Override
        public String convertSave(Rank t) {
            return t.getName();
        }

        @Override
        public Rank convertLoad(String v) {
            return Rank.valueOf(v);
        }
    }

    public static class DateConverter extends TypeConverter<Date, Date> {

        @Override
        public Date convertSave(Date t) {
            return t;
        }

        @Override
        public Date convertLoad(Date v) {
            return v;
        }
    }

    public static class LocationConverter extends TypeConverter<List, Location> {

        @Override
        public Location convertLoad(List t) {
            double x, y, z;
            float pitch = 0, yaw = 0;
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
            if(t.size() >= 5) {
                if(t.get(3) instanceof Integer) {
                    pitch = ((Integer)t.get(3)).floatValue();
                }
                else {
                    pitch = ((Double)t.get(3)).floatValue();
                }
                if(t.get(4) instanceof Integer) {
                    yaw = ((Integer)t.get(4)).floatValue();
                }
                else {
                    yaw = ((Double)t.get(4)).floatValue();
                }
            }
            world = (t.size() % 2 == 0) ? Bukkit.getWorld((String) t.get(t.size() - 1)) : SpleefLeague.DEFAULT_WORLD;
            return t.size() < 5 ? new Location(world, x, y, z) : new Location(world, x, y, z, pitch, yaw);
        }

        @Override
        public List convertSave(Location v) {
            List bdbl = new ArrayList();
            bdbl.add(v.getX());
            bdbl.add(v.getY());
            bdbl.add(v.getZ());
            if (v.getWorld() != SpleefLeague.DEFAULT_WORLD) {
                bdbl.add(v.getWorld().getName());
            }
            return bdbl;
        }
    }
    
    public static class HashSetStringConverter extends TypeConverter<List, HashSet<String>> {

        @Override
        public HashSet<String> convertLoad(List t) {
            HashSet<String> hs = new HashSet<>();
            for (Object o : t) {
                hs.add((String) o);
            }
            return hs;
        }

        @Override
        public List convertSave(HashSet<String> v) {
            List bdbl = new ArrayList();
            for (String s : v) {
                bdbl.add(s);
            }
            return bdbl;
        }
    }
    
    public static class HashSetIntegerConverter extends TypeConverter<List, HashSet<Integer>> {

        @Override
        public HashSet<Integer> convertLoad(List t) {
            HashSet<Integer> hs = new HashSet<>();
            for (Object o : t) {
                hs.add((Integer) o);
            }
            return hs;
        }

        @Override
        public List convertSave(HashSet<Integer> v) {
            List bdbl = new ArrayList();
            for (Integer s : v) {
                bdbl.add(s);
            }
            return bdbl;
        }
    }
}
