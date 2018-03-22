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
public class AbstractInventoryMenuComponent {
    
    private final AbstractInventoryMenu parent;
    
    public AbstractInventoryMenuComponent(AbstractInventoryMenu parent) {
        this.parent = parent;
    }
    
    public AbstractInventoryMenu getParent() {
        return parent;
    }
}
