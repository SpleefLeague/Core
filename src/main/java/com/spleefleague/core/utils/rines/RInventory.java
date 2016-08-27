package com.spleefleague.core.utils.rines;

import com.spleefleague.core.SpleefLeague;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * This class represents an inventory (but doesn't extend bukkit's inventory) and consists of items with onClick event.
 * @author RinesThaix
 */
public class RInventory {
    
    @Getter
    private final Inventory inventory;
    
    @Getter
    private final String name;
    
    @Getter
    private final Map<Integer, RItem> items = new HashMap<>();
    
    private int previousIndex = 0;

    /**
     * Creates new RInventory (instance) with colored specified name & specified size (should be dividable by 9).
     * @param name name of RInventory (bukkit's inventory).
     * @param sizeInLines size of the inventory.
     */
    public RInventory(String name, int sizeInLines) {
        this.name = UtilChat.c(name);
        this.inventory = Bukkit.createInventory(null, sizeInLines * 9, this.name);
    }
    
    /**
     * Adds item to the specified row & column (line and line-slot) in the inventory.
     * For example, 9th slot located in 1st row & 9th column;
     * 15th slot - in 2nd row & 7th column.
     * @param item the item to be added.
     * @param row row in bukkit's inventory (numeration starts from 1).
     * @param column column in bukkit' inventory (numberation starts from 1).
     */
    public void addItem(RItem item, int row, int column) {
        int slot = (row - 1) * 9 + column - 1;
        inventory.setItem(slot, item.getItem());
        items.put(slot, item);
    }
    
    /**
     * Adds item to the first empty slot in the inventory.
     * @param item the item to be added.
     */
    public void addItem(RItem item) {
        addItem(item, previousIndex++);
    }
    
    /**
     * Adds item to the specified slot in the inventory.
     * @param item the item to place.
     * @param index id of bukkit's inventory's slot to be placed to.
     */
    public void addItem(RItem item, int index) {
        if(index >= inventory.getSize()) {
            SpleefLeague.getInstance().getLogger().log(Level.WARNING, "Tried to add more items to RInventory than it can contain!");
            return;
        }
        inventory.setItem(index, item.getItem());
        items.put(index, item);
    }
    
    /**
     * Updates item icon in bukkit's inventory at specified slot (based on current RItem's icon).
     * @param slot id of bukkit's inventory's slot to work with.
     */
    public void updateItem(int slot) {
        inventory.setItem(slot, items.get(slot).getItem());
    }
    
    void openInventory(Player p) {
        p.openInventory(inventory);
    }
    
    /**
     * Returns RItem at specified slot in the bukkit's inventory.
     * @param slot id of bukkit's inventory in which item we're looking for is.
     * @return RItem located at specified slot in bukkit's inventory.
     */
    public RItem get(int slot) {
        return items.get(slot);
    }
    
}
