/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.debug;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.utils.Area;
import com.spleefleague.core.utils.Debugger;
import com.spleefleague.core.utils.fakeblock.MultiBlockChangeUtil;
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
public class GA implements Debugger {
    
    @Override
    public void debug() {
        World w = SpleefLeague.DEFAULT_WORLD;
        List<Location> available = new ArrayList<>();
//        int minX = -54, maxX = -30;
//        int minY = 2, maxY = 13;
//        int minZ = 206, maxZ = 247;
        
        int minX = -16, maxX = -15;
        int minY = 12, maxY = 13;
        int minZ = 223, maxZ = 224;
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(w, x, y, z);
                    if(loc.getBlock().getType() == Material.SNOW_BLOCK) {
                        available.add(new Location(w, x, y, z));
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
                area.getLow().add(1, 0, 0);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh(), area.getLow().add(-1, 0, 0));
            do {
                area.getLow().add(0, 1, 0);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh(), area.getLow().add(0, -1, 0));
            do {
                area.getLow().add(0, 0, 1);
                area = new Area(area.getHigh(), area.getLow());
            } while(isAllSnow(area.getBlocks()));
            area = new Area(area.getHigh(), area.getLow().add(0, 0, -1));
            for(Block b : area.getBlocks()) {
                available.remove(b.getLocation());
            }
            areas.add(area);
        }
        Player p = Bukkit.getPlayer("Joba");
        for(Area a : areas) {
            p.sendMessage(a.getHigh().toString());
            p.sendMessage(a.getLow().toString());
            p.sendMessage("==");
            MultiBlockChangeUtil.changeBlocks(a.getBlocks(), Material.GLASS, p);
        }
        p.sendMessage("Done");
    }
    
    private boolean isAllSnow(Block[] blocks) {
        for(Block b : blocks) {
            if(b.getType() != Material.SNOW_BLOCK) {
                return false;
            }
        }
        return true;
    }
}