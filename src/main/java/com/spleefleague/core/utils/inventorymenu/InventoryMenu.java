package com.spleefleague.core.utils.inventorymenu;

import java.util.Map;
import java.util.OptionalInt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.spleefleague.core.SpleefLeague;

public class InventoryMenu extends InventoryMenuComponent implements InventoryHolder {

    private static final int ROWSIZE = 9;

    private final Inventory inventory;
    private final Map<Integer, InventoryMenuComponent> components;
    private final boolean exitOnClickOutside;
    private final boolean menuControls;

    public InventoryMenu(ItemStack displayItem, String title, Map<Integer, InventoryMenuComponent> components, boolean exitOnClickOutside, boolean menuControls) {
        super(displayItem);
        this.components = components;
        //System.out.println("creating inv " + title);
        this.inventory = Bukkit.createInventory(this, calcRows() * ROWSIZE, title);
        this.exitOnClickOutside = exitOnClickOutside;
        this.menuControls = menuControls;
        setParents();
        populateInventory();
    }

    private int calcRows() {
        OptionalInt oInt = components.keySet().stream().mapToInt(i -> i).max();
        int maxIndex = oInt.orElse(0);

        //Normaly it would be (size + ROWSIZE - 1 ) / ROWSIZE but since we have an index no -1
        //and btw its an integer divison round up thingy -> black magic
        int rows = (maxIndex + ROWSIZE) / ROWSIZE;
        return rows;
    }

    private void setParents() {
        components.values().forEach(component -> component.setParent(this));
    }

    private void populateInventory() {
        components.forEach((key, value) -> inventory.setItem(key, value.getDisplayItem()));
    }

    void addMenuControls() {
        if (menuControls) {
            InventoryMenuComponent rootComp = getRoot();

            if (rootComp instanceof InventoryMenu) {
                InventoryMenu rootMenu = (InventoryMenu) rootComp;

                if (getParent() != null) {
                    InventoryMenuItem mainMenuItem = InventoryMenuAPI.item()
                            .displayIcon(Material.MINECART)
                            .displayName(ChatColor.GREEN + "Main Menu")
                            .description("Click to back to the main menu")
                            .onClick(event -> rootMenu.open(event.getPlayer()))
                            .build().construct();

                    components.put(8, mainMenuItem);
                    inventory.setItem(8, mainMenuItem.getDisplayItem());

                    InventoryMenuItem goBackItem = InventoryMenuAPI.item()
                            .displayIcon(Material.ANVIL)
                            .displayName(ChatColor.GREEN + "Go back")
                            .description("Click to go back one menu level")
                            .onClick(event -> getParent().open(event.getPlayer()))
                            .build().construct();
                    components.put(0, goBackItem);

                    inventory.setItem(0, goBackItem.getDisplayItem());
                }
            }
        }

    }

    @Override
    public void selected(Player player) {
        open(player);
    }

    public void open(Player player) {
        Inventory current = player.getOpenInventory().getTopInventory();

        if (current == null) {
            player.openInventory(inventory);
        }
        else {
            player.closeInventory();

            //wait 1 Tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.openInventory(inventory);
                }
            }.runTask(SpleefLeague.getInstance());
        }

    }

    public void close(Player player) {
        if (inventory.getViewers().contains(player)) {
            player.closeInventory();
            //TODO: Needed?

            inventory.getViewers().forEach(p -> System.out.println(p.getName()));

            inventory.getViewers().remove(player);

            inventory.getViewers().forEach(p -> System.out.println(p.getName()));
        }
    }

    public void selectItem(Player p, int index) {
        if (components.containsKey(index)) {
            InventoryMenuComponent component = components.get(index);
            component.selected(p);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public boolean exitOnClickOutside() {
        return exitOnClickOutside;
    }
}
