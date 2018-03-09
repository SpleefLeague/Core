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
    SKIP_SINGLE_SUBMENU(3);
    
    private final int flag;
    
    private InventoryMenuFlag(int id) {
        this.flag = 1 << id;
    }
    
    public static boolean isSet(int flags, InventoryMenuFlag flag) {
        return (flags & flag.flag) != 0;
    }
    
    public static int set(int flags, InventoryMenuFlag flag) {
        return flags | flag.flag;
    }
    
    public static int unset(int flags, InventoryMenuFlag flag) {
        return flags & (~flag.flag);
    }
}
