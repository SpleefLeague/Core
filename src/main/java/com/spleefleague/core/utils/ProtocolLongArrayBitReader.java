/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import java.nio.ByteBuffer;

/**
 *
 * @author Jonas
 */
public class ProtocolLongArrayBitReader {
    
    private final byte[] data;
    private int offset = 0;
    
    public ProtocolLongArrayBitReader(byte[] data) {
        this.data = data;
    }
    
    public ProtocolLongArrayBitReader(short[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length * Short.SIZE / Byte.SIZE);
        for(short s : data) {
            buffer.putShort(s);
        }
        this.data = buffer.array();
    }
    
    public ProtocolLongArrayBitReader(int[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length * Integer.SIZE / Byte.SIZE);
        for(int i : data) {
            buffer.putInt(i);
        }
        this.data = buffer.array();
    }
    
    
    public ProtocolLongArrayBitReader(long[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length * Long.SIZE / Byte.SIZE);
        for(long l : data) {
            buffer.putLong(l);
        }
        this.data = buffer.array();
    }
    
    public long readLong(int bits) {
        long value = 0;
        int read = 0;
        while(bits > 0) {
            int toRead = Math.min(8, bits);
            int b = Byte.toUnsignedInt(readByte(toRead));
            b <<= read;
            value |= b;
            read += toRead;
            bits -= toRead;
        }
        return value;
    }
    
    public short readShort(int bits) {
        short value = 0;
        int read = 0;
        while(bits > 0) {
            int toRead = Math.min(8, bits);
            int b = Byte.toUnsignedInt(readByte(toRead));
            b <<= read;
            value |= b;
            read += toRead;
            bits -= toRead;
        }
        return value;
    }
    
    public int readInt(int bits) {
        int value = 0;
        int read = 0;
        while(bits > 0) {
            int toRead = Math.min(8, bits);
            int b = Byte.toUnsignedInt(readByte(toRead));
            b <<= read;
            value |= b;
            read += toRead;
            bits -= toRead;
        }
        return value;
    }
    
    public byte readByte(int bits) {
        int p = offset / 8, o = offset % 8;
        p = 7 - p % 8 + (p / 8) * 8;
        byte b =  (byte)(Byte.toUnsignedInt(data[p]) >>> o);
        int read = 8 - o;
        if(read > bits) {
            short ff = 0xFF;
            ff >>>= (8 - bits);
            b &= ff;
            offset += bits;
            return b;
        }
        offset += read;
        bits -= read;
        if(bits > 0) {
            byte c = readByte(bits);
            b |= (c << read);
        }
        return b;
    }
}
