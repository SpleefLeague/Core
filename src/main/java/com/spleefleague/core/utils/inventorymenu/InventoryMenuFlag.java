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
    IGNORE_PAGE_OVERFLOW(1),
    MENU_CONTROL(2),
    EXIT_ON_COMPLETE_DIALOG(3),
    EXIT_ON_NO_PERMISSION(4),
    SKIP_SINGLE_SUBMENU(5),
    CLOSE_EMPTY_SUBMENU(6, 2, 0b00), //6-7
    FORBID_EMPTY_SUBMENU(6, 2, 0b01),//6-7
    HIDE_EMPTY_SUBMENU(6, 2, 0b010); //6-7
    
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
