package com.spleefleague.core.utils.rines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Item that can be added to RInventory; it runs function onClick every time player clicks it.
 * @author RinesThaix
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class RItem {
    
    @Setter
    @Getter
    private ItemStack item;
    
    /**
     * This function runs every time player clicks that item.
     * @param p who clicked me.
     * @param slot id of clicked slot.
     */
    public abstract void onClick(Player p, int slot);
    
    public void onClick(Player p, ClickType clickType, int slot) {
        onClick(p, slot);
    }
    
}
