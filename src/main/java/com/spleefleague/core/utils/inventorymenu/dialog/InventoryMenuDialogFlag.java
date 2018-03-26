/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

/**
 *
 * @author jonas
 */
public enum InventoryMenuDialogFlag {
    EXIT_ON_COMPLETE_DIALOG(0);
    
    private final int flag, bitmask;
    
    private InventoryMenuDialogFlag(int id) {
        this.flag = 1 << id;
        this.bitmask = 1 << id;
    }
    
    private InventoryMenuDialogFlag(int id, int length, int flag) {
        this.bitmask = (-1 >>> (Integer.SIZE - length)) << id;
        this.flag = flag << id;
    }
    
    public static boolean isSet(int flags, InventoryMenuDialogFlag flag) {
        return (flags & flag.bitmask) == flag.flag;
    }
    
    public static int set(int flags, InventoryMenuDialogFlag flag) {
        return unset(flags, flag) | flag.flag;
    }
    
    public static int unset(int flags, InventoryMenuDialogFlag flag) {
        return flags & (~flag.bitmask);
    }
}
