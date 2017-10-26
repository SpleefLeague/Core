package com.spleefleague.core.utils.inventorymenu;

import java.util.Map;
import java.util.OptionalInt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.bukkit.event.inventory.ClickType;

public class InventoryMenu extends InventoryMenuComponent implements InventoryHolder {

    private static final int ROWSIZE = 9;

    private final Inventory inventory;
    private final Map<Integer, InventoryMenuComponent> allComponents;
    private final boolean exitOnClickOutside;
    private final boolean menuControls;
    private final SLPlayer slp;
    private final Map<Integer, InventoryMenuComponent> currentComponents;

    protected InventoryMenu(ItemStackWrapper displayItem, String title, Map<Integer, InventoryMenuComponent> components, boolean exitOnClickOutside, boolean menuControls, Dynamic<Boolean> accessController, Dynamic<Boolean> visibilityController, SLPlayer slp) {
        super(displayItem, visibilityController, accessController);
        this.slp = slp;
        this.allComponents = components;
        this.inventory = Bukkit.createInventory(this, calcRows() * ROWSIZE, title);
        this.exitOnClickOutside = exitOnClickOutside;
        this.menuControls = menuControls;
        this.currentComponents = new HashMap<>();
        setParents();
        addMenuControls();
        populateInventory();
    }

    public SLPlayer getOwner() {
        return slp;
    }

    private int calcRows() {
        OptionalInt oInt = allComponents.keySet().stream().mapToInt(i -> i).max();
        int maxIndex = oInt.orElse(0);

        //Normaly it would be (size + ROWSIZE - 1 ) / ROWSIZE but since we have an index no -1
        //and btw its an integer divison round up thingy -> black magic
        int rows = (Math.max(maxIndex, allComponents.size() - 1) + ROWSIZE) / ROWSIZE;
        return rows;
    }

    private void setParents() {
        allComponents.values().forEach(component -> component.setParent(this));
    }

    protected void populateInventory() {
        inventory.clear();
        currentComponents.clear();
        allComponents.entrySet().stream().filter((entry) -> (entry.getKey() >= 0 && entry.getValue().isVisible(slp))).forEach((entry) -> {
            currentComponents.put(entry.getKey(), entry.getValue());
        });
        int current = 0;
        for (int key : allComponents.keySet().stream().filter((key) -> key < 0 && allComponents.get(key).isVisible(slp)).sorted().collect(Collectors.toList())) {
            InventoryMenuComponent value = allComponents.get(key);
            while (currentComponents.containsKey(current)) {
                current++;
            }
            currentComponents.put(current, value);
        }
        currentComponents.forEach((key, value) -> inventory.setItem(key, value.getDisplayItemWrapper().construct(slp)));
    }

    protected void addMenuControls() {
        if (menuControls) {
            InventoryMenuComponent rootComp = getRoot();

            if (rootComp instanceof InventoryMenu) {
//                InventoryMenu rootMenu = (InventoryMenu) rootComp;

                if (getParent() != null) {
//                    InventoryMenuItem mainMenuItem = InventoryMenuAPI.item()
//                            .displayIcon(Material.MINECART)
//                            .displayName(ChatColor.GREEN + "Main Menu")
//                            .description("Click to back to the main menu")
//                            .onClick(event -> rootMenu.open())
//                            .build().construct(slp);
//
//                    allComponents.put(1, mainMenuItem);
//                    inventory.setItem(1, mainMenuItem.getDisplayItemWrapper().construct(slp));

                    InventoryMenuItem goBackItem = InventoryMenuAPI.item()
                            .displayIcon(Material.ANVIL)
                            .displayName(ChatColor.GREEN + "Go back")
                            .description("Click to go back one menu level")
                            .onClick(event -> {
                                if (getParent() != null) {
                                    getParent().open();
                                } else {
                                    event.getPlayer().closeInventory();
                                }
                            })
                            .build().construct(slp);
                    allComponents.put(0, goBackItem);
                    //inventory.setItem(0, goBackItem.getDisplayItemWrapper().construct(slp));
                }
            }
        }

    }

    @Override
    public void selected(ClickType clickType) {
        open();
    }

    public void open() {
        Player player = slp.getPlayer();
        if (!this.hasAccess(slp)) {
            player.sendMessage(ChatColor.RED + "You are not allowed to open this InventoryMenu");
        } else {
            player.openInventory(inventory);
        }
    }

    public void close(Player player) {
        if (inventory.getViewers().contains(player)) {
            player.closeInventory();
            inventory.getViewers().remove(player);
        }
    }

    public void selectItem(int index, ClickType clickType) {
        if (currentComponents.containsKey(index)) {
            InventoryMenuComponent component = currentComponents.get(index);
            if (component.hasAccess(slp)) {
                component.selected(clickType);
            } else {
                slp.closeInventory();
                slp.sendMessage(ChatColor.RED + "You don't have access to this");
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public boolean exitOnClickOutside() {
        return exitOnClickOutside;
    }

    public void update() {
        populateInventory();
    }
}
