/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.debug;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.DBSaveable;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.utils.Area;
import com.spleefleague.core.utils.Debugger;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
/**
 *
 * @author jonas
 */
public class Test implements Debugger {
    
    @Override
    public void debug() {
        World w = SpleefLeague.DEFAULT_WORLD;
        List<Location> available = new ArrayList<>();
        int minX = -54, maxX = -30;
        int minY = 2, maxY = 13;
        int minZ = 206, maxZ = 247;
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(w, x, y, z);
                    if(loc.getBlock().getType() == Material.SNOW_BLOCK) {
                        available.add(loc);
                    }
                }
            }
        }
        List<Area> areas = new ArrayList<>();
        while(!available.isEmpty()) {
            Location loc = available.get(0);
            available.remove(0);
            Area area = new Area(loc, loc);
            do {
                area.getLow().add(-1, 0, 0);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh(), area.getLow().add(1, 0, 0));
            do {
                area.getLow().add(0, -1, 0);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh(), area.getLow().add(0, 1, 0));
            do {
                area.getLow().add(0, 0, -1);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh(), area.getLow().add(0, 0, 1));
            do {
                area.getHigh().add(1, 0, 0);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh().add(-1, 0, 0), area.getLow());
            do {
                area.getHigh().add(0, 1, 0);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh().add(0, -1, 0), area.getLow());
            do {
                area.getHigh().add(0, 0, 1);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh().add(0, 0, -1), area.getLow());
            for(Block b : area.getBlocks()) {
                available.remove(b.getLocation());
            }
            areas.add(area);
        }
        EntityBuilder.save(new Temp(areas), SpleefLeague.getInstance().getMongo().getDatabase("SpleefLeague").getCollection("Settings"));
        Player p = Bukkit.getPlayer("Joba");
        p.sendMessage("" + areas.size());
    }
    
    private boolean isAllSnow(Block[] blocks) {
        for(Block b : blocks) {
            if(b.getType() != Material.SNOW_BLOCK) {
                return false;
            }
        }
        return true;
    }
    
    private class Temp extends DBEntity implements DBSaveable {
        @DBSave(fieldName = "field")
        private Area[] t;
        
        public Temp(List<Area> areas) {
            t = areas.toArray(new Area[areas.size()]);
        }
    }
}