/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.spleefleague.core.io.TypeConverter;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBLoadable;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.DBSaveable;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 *
 * @author Jonas
 */
public class Area extends DBEntity implements DBLoadable, DBSaveable {

    @DBLoad(fieldName = "low", typeConverter = TypeConverter.LocationConverter.class)
    @DBSave(fieldName = "low", typeConverter = TypeConverter.LocationConverter.class)
    private Location low;
    @DBLoad(fieldName = "high", typeConverter = TypeConverter.LocationConverter.class)
    @DBSave(fieldName = "high", typeConverter = TypeConverter.LocationConverter.class)
    private Location high;

    public Area(Location loc1, Location loc2) {
        setLocations(loc1, loc2);
    }

    //Private for now
    private void setLocations(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            throw new UnsupportedOperationException("Worlds have to be equal");
        }
        low = new Location(loc1.getWorld(), Math.min(loc1.getX(), loc2.getX()), Math.min(loc1.getY(), loc2.getY()), Math.min(loc1.getZ(), loc2.getZ()));
        high = new Location(loc1.getWorld(), Math.max(loc1.getX(), loc2.getX()), Math.max(loc1.getY(), loc2.getY()), Math.max(loc1.getZ(), loc2.getZ()));
    }

    public Location getHigh() {
        return high;
    }

    public Location getLow() {
        return low;
    }

    public boolean isInArea(Location loc) {
        if (loc == null || low == null || high == null) {
            return false;
        }
        if (loc.getWorld() == low.getWorld()) {
            if (loc.getX() >= low.getX() && loc.getX() <= high.getX()) {
                if (loc.getY() >= low.getY() && loc.getY() <= high.getY()) {
                    if (loc.getZ() >= low.getZ() && loc.getZ() <= high.getZ()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Block[] getBlocks() {
        return MultiBlockChangeUtil.getBlocksInArea(high, low).toArray(new Block[0]);
    }

    public static boolean isInAny(Location loc, Area... areas) {
        for (Area area : areas) {
            if (area.isInArea(loc)) {
                return true;
            }
        }
        return false;
    }

}
