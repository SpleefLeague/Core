/*
 * Copyright (c) 2013 All Right Reserved, http://www.multicu.be/
 */
package net.spleefleague.core.utils.menu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Codepanda
 */
public class Menu implements Listener {

    private String name;
    private int size;
    private HashMap<Integer, MenuItem> items;
    protected final Player player;
    private Inventory inv;
    private BukkitTask task;
    private boolean dead;
    private boolean closeOnClick;
    private MenuClickListener listener;

    public Menu(String name, Player p) {
        this.items = new HashMap<>();
        this.name = name;
        this.player = p;
        if (this.name.length() > 32) {
            this.name = this.name.substring(0, 29) + "..";
        }
    }
    
    public String getName() {
        return name;
    }

    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }
    
    public int[] getAvailableSlots(int amount) {
        int pointer = 0;
        int[] available = new int[amount];
        for(int i = 0; pointer < amount; i++) {
            if(isSlotFree(i)) {
                available[pointer] = i;
                pointer++;
            }
        }
        return available;
    }
    
    public boolean isSlotFree(int slot) {
        return !items.keySet().contains(slot);
    }

    public void show() {
        player.closeInventory();
        
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());

        task = new BukkitRunnable() {
            @Override
            public void run() {
                close();
            }
        }.runTaskLater(SpleefLeague.getInstance(), 20 * 60 * 5);

        create(player);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().equals(player)) {
            close();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getWhoClicked().equals(player)) {
            if (event.getCurrentItem() != null) {
                int slot = event.getSlot();
                MenuItem item = items.get(slot);
                if (item != null) {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                    event.setCursor(null);
                    if (listener != null) {
                        listener.onClick(event, item);
                    }
                    item.onClick(event);
                    if (this.closeOnClick) {
                        close();
                    }
                }
            }
        }
    }

    public void setListener(MenuClickListener listener) {
        this.listener = listener;
    }

    public boolean isDead() {
        return dead;
    }
    
    protected Collection<MenuItem> getMenuItems() {
        return items.values();
    }

    public void close() {
        if (dead) {
            return;
        }
        dead = true;
        HandlerList.unregisterAll(this);
        if (inv != null) {
            player.closeInventory();
        }
        if (task != null) {
            task.cancel();
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public MenuItem addItem(int slot, MenuItem item) {
        items.put(slot, item);
        return item;
    }
    
    public MenuItem addItem(MenuItem item) {
        return addItem(getAvailableSlots(1)[0], item);
    }

    public int getFittingSize() {
        int ps = 1;
        for (Integer i : items.keySet()) {
            if (i > ps) {
                ps = i;
            }
        }
        ++ps;
        if (size > (ps + 1)) {
            ps = size;
        }

        if (ps % 9 == 0) {
            return ps;
        }

        double x = Math.ceil(ps / 9);
        double next = (x + 1) * 9;
        int rest = (int) (next - ps);

        return (int) next;
    }

    private void create(Player p) {
        Iterator<Map.Entry<Integer, MenuItem>> it = items.entrySet().iterator();
        inv = Bukkit.createInventory(p, getFittingSize(), name);
        while (it.hasNext()) {
            Map.Entry<Integer, MenuItem> next = it.next();
            inv.setItem(next.getKey(), next.getValue().buildItem(p));
        }
    }
    
    public void clear() {
        items.clear();
        inv.clear();
    }
    
    public void update() {
        inv.clear();
        Iterator<Map.Entry<Integer, MenuItem>> it = items.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, MenuItem> next = it.next();
            inv.setItem(next.getKey(), next.getValue().buildItem(player));
        }
    }
}
