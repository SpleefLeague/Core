/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import java.util.Arrays;
import java.util.Collection;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Jonas
 */
public class FakeBlock extends FakeArea {
    
    private final Location location;
    private Material material;
    
    public FakeBlock(Location location, Material material) {
        this.location = location;
        this.material = material;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Chunk getChunk() {
        return location.getChunk();
    }
    
    public Material getType() {
        return material;
    }
    
    public void setType(Material material) {
        this.material = material;
    }
    
    public int getX() {
        return location.getBlockX();
    }
    
    public int getY() {
        return location.getBlockY();
    }
    
    public int getZ() {
        return location.getBlockZ();
    }
    
    @Override
    public Collection<FakeBlock> getBlocks() {
        return Arrays.asList(this);
    }
}
