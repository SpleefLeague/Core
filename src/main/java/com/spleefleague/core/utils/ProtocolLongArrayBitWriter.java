/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

/**
 *
 * @author Jonas
 */
public class ProtocolLongArrayBitWriter {
    
    private final byte[] data;
    private int offset = 0;
    
    public ProtocolLongArrayBitWriter(byte[] data) {
        this.data = data;
    }
    
    public void writeLong(long l, int bits) {
        while(bits > 0) {
            int toWrite = Math.min(8, bits);
            bits -= toWrite;
            short ff = 0xFF;
            if(toWrite == 8) {
                writeByte((byte)(l & ff), toWrite);
            }
            else {
                ff >>>= 8 - toWrite;
                writeByte((byte)(l & ff), toWrite);
            }
            l >>>= 8;
        }
    }
    
    public void writeShort(short s, int bits) {
        while(bits > 0) {
            int toWrite = Math.min(8, bits);
            bits -= toWrite;
            short ff = 0xFF;
            if(toWrite != 8) {
                ff >>>= 8 - toWrite;
            }
            writeByte((byte)(s & ff), toWrite);
            s >>>= 8;
        }
    }
    
    public static String binaryShort(short b) {
        return String.format("%16s", Integer.toBinaryString(Short.toUnsignedInt(b))).replace(' ', '0');
    }
    
    public static String binaryByte(byte b) {
        return String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(b))).replace(' ', '0');
    }
    
    public void writeInt(int i, int bits) {
        while(bits > 0) {
            int toWrite = Math.min(8, bits);
            bits -= toWrite;
            short ff = 0xFF;
            if(toWrite == 8) {
                writeByte((byte)(i & ff), toWrite);
            }
            else {
                ff >>>= 8 - toWrite;
                writeByte((byte)(i & ff), toWrite);
            }
            i >>>= 8;
        }
    }
    
    public void writeByte(byte b, int bits) {
        if(bits == 0) return;
        int p = offset / 8, o = offset % 8;
        p = 7 - p % 8 + (p / 8) * 8;
        int space = 8 - o;
        if(space >= bits) {
            short ff = 0xFF;
            ff >>>= (8 - bits);
            data[p] |= (b & ff) << o;
            offset += bits;
        }
        else {
            offset += space;
            bits -= space;
            short ff = 0xFF;
            ff >>>= o;
            data[p] |= (b & ff) << o;
            b >>>= space;
            writeByte(b, bits);
        }
    }
}
