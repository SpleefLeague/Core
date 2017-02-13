/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

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
    private final int chunkx, chunkz;
    private Material material;
    private byte data;
    
    public FakeBlock(Location location, Material material) {
        this(location, material, (byte)0);
    }
    
    public FakeBlock(Location location, Material material, byte data) {
        this.location = location;
        this.material = material;
        Chunk chunk = location.getChunk();
        chunkx = chunk.getX();
        chunkz = chunk.getZ();
        this.data = data;
    }

    public Location getLocation() {
        return location;
    }

    public Chunk getChunk() {
        return location.getChunk();
    }

    public int getChunkX() {
        return chunkx;
    }

    public int getChunkZ() {
        return chunkz;
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
    
    public byte getDamageValue() {
        return data;
    }
    
    public void setDamageValue(byte data) {
        this.data = data;
    }

    @Override
    public Collection<FakeBlock> getBlocks() {
        return Arrays.asList(this);
    }
}