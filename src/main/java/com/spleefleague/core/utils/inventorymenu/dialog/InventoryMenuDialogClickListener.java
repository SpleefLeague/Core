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
public interface InventoryMenuDialogClickListener<B> {
    
    void onClick(InventoryMenuDialogClickEvent<B> event);
}
