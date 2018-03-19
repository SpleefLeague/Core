/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 */
public interface Selectable {
    
    public void selected(ClickType clickType);
}
