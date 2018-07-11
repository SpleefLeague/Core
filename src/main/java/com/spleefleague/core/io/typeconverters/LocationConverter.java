package com.spleefleague.core.io.typeconverters;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.entitybuilder.TypeConverter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author balsfull
 */
public class LocationConverter extends TypeConverter<List, Location> {

    @Override
    public Location convertLoad(List l) {
        List t = new ArrayList<>();
        for (Object o : l) {
            if (o instanceof Long) {
                t.add(((Long) o).intValue());//Why is this even necessary?
            }
            else {
                t.add(o);
            }
        }
        double x, y, z;
        float pitch = 0, yaw = 0;
        World world;
        if (t.get(0) instanceof Integer) {
            x = (Integer) t.get(0);
        }
        else {
            x = (double) t.get(0);
        }
        if (t.get(1) instanceof Integer) {
            y = (Integer) t.get(1);
        }
        else {
            y = (double) t.get(1);
        }
        if (t.get(2) instanceof Integer) {
            z = (Integer) t.get(2);
        }
        else {
            z = (double) t.get(2);
        }
        if (t.size() >= 5) {
            if (t.get(3) instanceof Integer) {
                pitch = ((Integer) t.get(3)).floatValue();
            }
            else {
                pitch = ((Double) t.get(3)).floatValue();
            }
            if (t.get(4) instanceof Integer) {
                yaw = ((Integer) t.get(4)).floatValue();
            }
            else {
                yaw = ((Double) t.get(4)).floatValue();
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
        if(v.getYaw() != 0.0 || v.getPitch() != 0.0) {
            bdbl.add(v.getYaw());
            bdbl.add(v.getPitch());
        }
        if (v.getWorld() != SpleefLeague.DEFAULT_WORLD) {
            bdbl.add(v.getWorld().getName());
        }
        return bdbl;
    }
}