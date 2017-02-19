/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

/**
 *
 * @author Jonas
 */
public class ChunkData {
    
    private final ChunkSection[] sections;
    private final byte[] additionalData; //Should be biome data
    
    public ChunkData(ChunkSection[] sections, byte[] additionalData) {
        this.sections = sections;
        this.additionalData = additionalData;
    }
    
    public ChunkSection[] getSections() {
        return sections;
    }
    
    public byte[] getAdditionalData() {
        return additionalData;
    }
}
