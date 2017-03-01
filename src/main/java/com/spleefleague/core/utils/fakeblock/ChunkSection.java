/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

/**
 *
 * @author Jonas
 */
public class ChunkSection {
    
    private final BlockData[] blocks;
    private final byte[] lightData;
    private boolean modified = false;
    private BlockData[] paletteBlocks;
    private final Set<BlockData> paletteBlockSet;
    
    /**
     * 
     * @param blockdata Block data array described as in http://wiki.vg/SMP_Map_Format
     * @param lightData The chunk's original lighting data
     * @param palette Block palette object
     */
    public ChunkSection(byte[] blockdata, byte[] lightData, BlockPalette palette) {
        this.blocks = palette.decode(blockdata);
        paletteBlocks = palette.getBlocks();//Null for the global palette
        if(paletteBlocks != null) {
            paletteBlockSet = new HashSet<>();
            for(BlockData data : paletteBlocks) {
                paletteBlockSet.add(data);
            }
        }
        else {
            paletteBlockSet = null;
        }
        this.lightData = lightData;
    }
    
    protected ChunkSection(boolean overworld) {
        BlockData air = new BlockData(Material.AIR, (byte)0);
        blocks = new BlockData[4096];
        Arrays.fill(blocks, air);
        //An empty, unsent chunksection contains air blocks
        paletteBlocks = new BlockData[]{air};
        paletteBlockSet = new HashSet<>();
        paletteBlockSet.add(air);
        lightData = new byte[overworld ? 4096 : 2048];
        Arrays.fill(lightData, (byte)-1);//Default light data, everything is bright
    }
    
    public BlockData getBlockRelative(int x, int y, int z) {
        return blocks[x + z * 16 + y * 256];
    }
    
    public void setBlockRelative(BlockData data, int x, int y, int z) {
        blocks[x + z * 16 + y * 256] = data;
        modified = true;
        if(paletteBlockSet != null) {
            if(!paletteBlockSet.contains(data)) {
                paletteBlocks = null;
            }
            paletteBlockSet.add(data);
        }
    }
    
    public BlockData[] getBlockData() {
        return blocks;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public BlockData[] getContainedBlocks() {
        if(paletteBlocks == null) {
            if(paletteBlockSet == null) {
                return null;
            }
            paletteBlocks = paletteBlockSet.toArray(new BlockData[0]);
        }
        return paletteBlocks;
    }
    
    public byte[] getLightingData() {
        return lightData;
    }
}
