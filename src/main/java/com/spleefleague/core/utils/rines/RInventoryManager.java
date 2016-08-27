package com.spleefleague.core.utils.rines;

import com.spleefleague.core.SpleefLeague;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author RinesThaix
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RInventoryManager implements Listener {

    private final static Map<String, RInventory> opened = new HashMap<>();
    private final static Set<Integer> blockedSlots = new HashSet<>();
    
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new RInventoryManager(), SpleefLeague.getInstance());
    }
    
    /**
     * Opens RInventory for specified player (and closes previous bukkit's inventory before this).
     * @param p target player.
     * @param hinv target RInventory.
     */
    public static void openInventory(Player p, RInventory hinv) {
        p.closeInventory();
        opened.put(p.getName(), hinv);
        hinv.openInventory(p);
    }
    
    /**
     * Returns player's active RInventory (if there any, otherways returns null).
     * @param p target player.
     * @return player's active RInventory (if there any, otherways returns null).
     */
    public static RInventory getOpened(Player p) {
        return opened.get(p.getName());
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String name = e.getWhoClicked().getName();
        RInventory hinv = opened.get(name);
        Player p = (Player) e.getWhoClicked();
        if(hinv != null) {
            Inventory top = e.getView().getTopInventory();
            if(top.getName().equals(hinv.getName())) {
                e.setCancelled(true);
                int slot = e.getRawSlot();
                if(slot >= 0 && slot < top.getSize() && top.getItem(slot) != null) {
                    RItem hitem = hinv.get(slot);
                    if(hitem != null)
                        hitem.onClick(p, e.getClick(), slot);
                }
            }
        }
        if(e.getClickedInventory() == e.getView().getBottomInventory() && isBlocked(p, e.getSlot(), e.getHotbarButton()))
            e.setCancelled(true);
    }
    
    private boolean isBlocked(Player p, int slot, int hotbar) {
        return blockedSlots.contains(slot) || blockedSlots.contains(hotbar);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String name = e.getPlayer().getName();
        opened.remove(name);
    }
    
    /**
     * Blocks slot from any uses (inventory clicks) for all players; be aware of that the slot is still droppable.
     * @param id id of bukkit's inventory's slot.
     */
    public static void addBlockedSlot(int id) {
        blockedSlots.add(id);
    }
    
    /**
     * Unblocks slot from addBlockedSlot(int) function.
     * @param id id of bukkit's inventory's slot.
     */
    public static void removeBlockedSlot(int id) {
        blockedSlots.remove(id);
    }
    
}
