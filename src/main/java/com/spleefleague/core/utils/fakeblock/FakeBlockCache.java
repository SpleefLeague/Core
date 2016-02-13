/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

import com.spleefleague.core.utils.FakeArea;
import com.spleefleague.core.utils.FakeBlock;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FakeBlockCache {
    
    private final Map<Integer, FakeChunk> map = new HashMap<>();
    
    public FakeBlockCache() {
        
    }
    
    public void clear() {
        map.clear();
    }
    
    public Set<FakeBlock> getBlocks(int[] x, int[] z) {
        Set<FakeBlock> blocks = new HashSet<>();
        for(int i = 0; i < x.length && i < z.length; i++) {
            Collection<FakeBlock> chunk = getBlocks(x[i], z[i]);
            if(chunk != null) {
                blocks.addAll(chunk);
            }
        }
        return blocks.isEmpty() ? null : blocks;
    }
    
    public Set<FakeBlock> getBlocks(int x, int z) {
        int key = getKey(x, z);  
        if(map.containsKey(key)) {
            FakeChunk chunk = map.get(key);  
            return chunk == null ? null : chunk.getBlocks();
        }
        return null;
    }
    
    public void addBlocks(FakeBlock... blocks) {
        for(FakeBlock block : blocks) {
            int x = block.getChunkX(), z = block.getChunkZ();
            int key = getKey(x, z);
            FakeChunk chunk;
            if(map.containsKey(key)) {
                chunk = map.get(key);
            }
            else {
                chunk = new FakeChunk(x, z);
                map.put(key, chunk);
            }
            chunk.addBlocks(block);
        }
    }
    
    public void addBlocks(Collection<FakeBlock> blocks) {
        for(FakeBlock block : blocks) {
            int x = block.getChunkX(), z = block.getChunkZ();
            int key = getKey(x, z);
            FakeChunk chunk;
            if(map.containsKey(key)) {
                chunk = map.get(key);
            }
            else {
                chunk = new FakeChunk(x, z);
                map.put(key, chunk);
            }
            chunk.addBlocks(block);
        }
    }
    
    public void addArea(FakeArea area) {
        addBlocks(area.getBlocks());
    }
    
    public void removeBlocks(FakeBlock... blocks) {
        for(FakeBlock block : blocks) {
            int x = block.getChunkX(), z = block.getChunkZ();
            int key = getKey(x, z);
            if(map.containsKey(key)) {
                map.get(key).removeBlocks(block);
            }
        }
    }
    
    public void removeBlocks(Collection<FakeBlock> blocks) {
        for(FakeBlock block : blocks) {
            int x = block.getChunkX(), z = block.getChunkZ();
            int key = getKey(x, z);
            if(map.containsKey(key)) {
                map.get(key).removeBlocks(block);
            }
        }
    }
    
    public void removeArea(FakeArea area) {
        removeBlocks(area.getBlocks());
    }
    
    private static int getKey(int x, int z) {
        return x + (z << 16);
    }
}