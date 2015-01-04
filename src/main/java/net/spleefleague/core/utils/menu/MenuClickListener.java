/*
 * Copyright (c) 2013 All Right Reserved, http://www.multicu.be/
 */
package net.spleefleague.core.utils.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Codepanda
 */
public interface MenuClickListener {

    public void onClick(InventoryClickEvent event, MenuItem item);
}
