package com.spleefleague.core.utils.rines;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents RItem with easy-to-use item's icon constructors.
 * Constructors with name specified place '&a' (green color char) right before it.
 * @author RinesThaix
 */
public abstract class RButton extends RItem {
    
    public RButton(Material icon, String name, List<String> description) {
        super(new SimpleItemStack(icon, "&a" + name, description).applyFlags(ItemFlag.values()));
    }
    
    public RButton(Material icon, int data, String name, List<String> description) {
        super(new SimpleItemStack(icon, "&a" + name, description, (short) data).applyFlags(ItemFlag.values()));
    }
    
    public RButton(ItemStack is) {
        super(is);
    }

}
