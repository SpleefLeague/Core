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
public enum InventoryMenuFlag {
    EXIT_ON_CLICK_OUTSIDE(0),
    MENU_CONTROL(1),
    SKIP_SINGLE_SUBMENU(2),
    CLOSE_EMPTY_SUBMENU(3, 2, 0b00),
    FORBID_EMPTY_SUBMENU(3, 2, 0b01),
    HIDE_EMPTY_SUBMENU(3, 2, 0b010);
    
    private final int flag, bitmask;
    
    private InventoryMenuFlag(int id) {
        this.flag = 1 << id;
        this.bitmask = 1 << id;
    }
    
    private InventoryMenuFlag(int id, int length, int flag) {
        this.bitmask = (-1 >>> (Integer.SIZE - length)) << id;
        this.flag = flag << id;
    }
    
    public static boolean isSet(int flags, InventoryMenuFlag flag) {
        return (flags & flag.bitmask) == flag.flag;
    }
    
    public static int set(int flags, InventoryMenuFlag flag) {
        return unset(flags, flag) | flag.flag;
    }
    
    public static int unset(int flags, InventoryMenuFlag flag) {
        return flags & (~flag.bitmask);
    }
}
