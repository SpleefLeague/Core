/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

import com.spleefleague.core.utils.ProtocolLongArrayBitReader;
import com.spleefleague.core.utils.ProtocolLongArrayBitWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

/**
 *
 * @author Jonas
 */
public abstract class BlockPalette {
    
    public static final BlockPalette GLOBAL = GlobalBlockPalette.instance();
    
    public abstract BlockData[] decode(byte[] data);
    public abstract Set<BlockData> getBlocks();
    public abstract int getBitsPerBlock();
    public abstract int getLength();
    public abstract int[] getPaletteData();
    public abstract byte[] encode(BlockData[] data);
    
    public static BlockPalette createPalette(int[] data, int bitsPerBlock) {
        return new EncodedBlockPalette(data, bitsPerBlock);
    }
    
    public static BlockPalette createPalette(BlockData[] data) {
        int bitsPerBlock = Math.max(31 - Integer.numberOfLeadingZeros(data.length), 4);
        if(bitsPerBlock < 9) {
            return new EncodedBlockPalette(data, bitsPerBlock);
        }
        else {
            return GLOBAL;
        }
    }
    
    private static class GlobalBlockPalette extends BlockPalette {

        @Override
        public BlockData[] decode(byte[] data) {
            ProtocolLongArrayBitReader reader = new ProtocolLongArrayBitReader(data);
            BlockData[] bdata = new BlockData[4096];//Chunk section is 16x16x16
            for (int i = 0; i < bdata.length; i++) {
                byte damage = reader.readByte(4);
                int id = reader.readInt(9);
                bdata[i] = new BlockData(Material.getMaterial(id), damage);
            }
            return bdata;
        }
        
        public static GlobalBlockPalette instance() {
            return new GlobalBlockPalette();
        }
        
        @Override
        public Set<BlockData> getBlocks() {
            return null;
        }

        @Override
        public int getBitsPerBlock() {
            return 13;
        }

        @Override
        public byte[] encode(BlockData[] data) {
            byte[] array = new byte[6656];
            ProtocolLongArrayBitWriter writer = new ProtocolLongArrayBitWriter(array);
            for(BlockData block : data) {
                writer.writeByte(block.getDamage(), 4);
                writer.writeInt(block.getType().getId(), 9);
            }
            return array;
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public int[] getPaletteData() {
             throw new UnsupportedOperationException("Not supported.");
        }
    }
    
    private static class EncodedBlockPalette extends BlockPalette {
        
        private final BlockData[] lookupTable;
        private final int bitsPerBlock;
        
        public EncodedBlockPalette(int[] data, int bitsPerBlock) {
            this.bitsPerBlock = bitsPerBlock;
            this.lookupTable = createLookupTable(data);
        }
        
        public EncodedBlockPalette(BlockData[] lookupTable, int bitsPerBlock) {
            this.lookupTable = lookupTable;
            this.bitsPerBlock = bitsPerBlock;
        }
        
        private BlockData[] createLookupTable(int[] data) {
            BlockData[] lookupTable = new BlockData[data.length];
            for(int i = 0; i < data.length; i++) {
                byte damage = (byte) (data[i] & 0xF);
                int id = data[i] >> 4;
                lookupTable[i] = new BlockData(Material.getMaterial(id), damage);
            }
            return lookupTable;
        }
        
        @Override
        public BlockData[] decode(byte[] data) {
            ProtocolLongArrayBitReader reader = new ProtocolLongArrayBitReader(data);
            BlockData[] array = new BlockData[4096];
            for(int i = 0; i < array.length; i++) {
                array[i] = lookupTable[reader.readShort(bitsPerBlock)];
            }
            return array;
        }

        @Override
        public Set<BlockData> getBlocks() {
            return new HashSet<>(Arrays.asList(lookupTable));
        }

        @Override
        public int getBitsPerBlock() {
            return bitsPerBlock;
        }

        @Override
        public byte[] encode(BlockData[] data) {
            byte[] array = new byte[512 * bitsPerBlock];
            ProtocolLongArrayBitWriter writer = new ProtocolLongArrayBitWriter(array);
            HashMap<BlockData, Integer> lookup = new HashMap<>();
            for(int i = 0; i < lookupTable.length; i++) {
                lookup.put(lookupTable[i], i);
            }
            for(BlockData block : data) {
                writer.writeInt(lookup.get(block), bitsPerBlock);
            }
            return array;
        }

        @Override
        public int[] getPaletteData() {
            int[] data = new int[lookupTable.length];
            for(int i = 0; i < data.length; i++) {
                data[i] = lookupTable[i].getDamage() & 0xF;
                data[i] |= lookupTable[i].getType().getId() << 4;
            }
            return data;
        }
        
        @Override
        public int getLength() {
            return lookupTable.length;
        }
    }
}
