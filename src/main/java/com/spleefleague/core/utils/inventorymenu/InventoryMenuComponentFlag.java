/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

/**
 *
 * @author jonas
 */
public enum InventoryMenuComponentFlag {
    IGNORE_PAGE_OVERFLOW(0),
    EXIT_ON_NO_PERMISSION(4);
    
    private final int flag, bitmask;
    
    private InventoryMenuComponentFlag(int id) {
        this.flag = 1 << id;
        this.bitmask = 1 << id;
    }
    
    private InventoryMenuComponentFlag(int id, int length, int flag) {
        this.bitmask = (-1 >>> (Integer.SIZE - length)) << id;
        this.flag = flag << id;
    }
    
    public static boolean isSet(int flags, InventoryMenuComponentFlag flag) {
        return (flags & flag.bitmask) == flag.flag;
    }
    
    public static int set(int flags, InventoryMenuComponentFlag flag) {
        return unset(flags, flag) | flag.flag;
    }
    
    public static int unset(int flags, InventoryMenuComponentFlag flag) {
        return flags & (~flag.bitmask);
    }
}
