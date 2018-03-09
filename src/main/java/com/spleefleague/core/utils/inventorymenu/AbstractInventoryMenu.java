package com.spleefleague.core.utils.inventorymenu;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.collections.MapUtil;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractInventoryMenu extends InventoryMenuComponent implements InventoryHolder {

    private static final int ROWSIZE = 9;
    private static final int COLUMNSIZE = 6;
    private static final int PAGE_NAVIGATION_SIZE = ROWSIZE * 2;
    private final int MAX_PAGE_SIZE = ROWSIZE * COLUMNSIZE;

    private final TreeMap<Integer, Inventory> inventories;
    private final Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> allComponents, staticComponents;
    private final String title;
    private final SLPlayer slp;
    private final Map<Integer, Map<Integer, InventoryMenuComponent>> currentComponents;
    private final int pagesize = ROWSIZE * COLUMNSIZE - PAGE_NAVIGATION_SIZE;
    private final int flags;
    private int currentPage = 0;
    
    protected AbstractInventoryMenu(
            ItemStackWrapper displayItem, 
            String title, 
            Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> components, 
            Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> staticComponents, 
            Function<SLPlayer, Boolean> accessController, 
            Function<SLPlayer, Boolean> visibilityController, 
            SLPlayer slp, 
            int flags) {
        super(displayItem, visibilityController, accessController, InventoryMenuFlag.isSet(flags, InventoryMenuFlag.IGNORE_PAGE_OVERFLOW));
        this.flags = flags;
        this.slp = slp;
        this.allComponents = components;
        this.staticComponents = staticComponents;
        this.inventories = new TreeMap<>();
        this.title = title;
        this.currentComponents = new HashMap<>();
        addMenuControls();
        populateInventory();
    }

    public SLPlayer getOwner() {
        return slp;
    }
    
    private void setParents() {
        currentComponents
                .values()
                .stream()
                .flatMap(m -> m.values().stream())
                .forEach(component -> component.setParent(this));
    }

    protected void populateInventory() {
        int highestDefined = 0;
        int count = 0;
        for(Entry<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> e : allComponents.entrySet()) {
            if(!e.getValue().isVisible(slp)) continue;
            count++;
            highestDefined = Math.max(highestDefined, e.getKey());
        }
        boolean multiPage = Math.max(count, highestDefined) > pagesize;
        
        TreeMap<Integer, Map<Integer, InventoryMenuComponent>> componentPageMap = generateComponentPageMap(allComponents);
        
        Queue<InventoryMenuComponent> fillupQueue = new LinkedList<>(allComponents
                .keySet()
                .stream()
                .sorted((i1, i2) -> Integer.compare(i2, i1))
                .filter(key -> key < 0)
                .map(key -> allComponents.get(key).construct(slp))
                .filter(m -> m.isVisible(slp))
                .collect(Collectors.toList()));
        for(int page = 0; !fillupQueue.isEmpty(); page++) {
            Map<Integer, InventoryMenuComponent> slots = componentPageMap.getOrDefault(page, new HashMap<>());
            if(slots.size() >= pagesize) continue;
            for (int slot = 0; !fillupQueue.isEmpty() && slot < pagesize; slot++) {
                if(slots.containsKey(slot)) continue;
                slots.put(slot, fillupQueue.poll());
            }
            componentPageMap.put(page, slots);
        }
        //Compressing menu page structure ([2,3,6,8,9] -> [0,1,2,3,4])
        componentPageMap.put(-1, null);
        componentPageMap = MapUtil.compress(componentPageMap);
        componentPageMap.remove(-1);
        componentPageMap.values()
                .stream()
                .forEach(m -> {
                    staticComponents
                            .forEach((i, imct) -> m.put(i, imct.construct(slp)));
                });
        //Adding page navigation
        if(multiPage) {
            int max = componentPageMap.lastKey();
            for (int page = 0; page <= max; page++) {
                Map<Integer, InventoryMenuComponent> slots = componentPageMap.get(page);
                if(page > 0) {
                    InventoryMenuComponent lastPage = createPreviousPageItem(page).construct(slp);
                    lastPage.setParent(this);
                    slots.put(MAX_PAGE_SIZE - 9, lastPage);
                }
                if(page < max) {
                    InventoryMenuComponent nextPage = createNextPageItem(page).construct(slp);
                    nextPage.setParent(this);
                    slots.put(MAX_PAGE_SIZE - 1, createNextPageItem(page).construct(slp));
                }
            }
        }
        //Creating Bukkit inventories
        inventories.clear();
        currentComponents.clear();
        currentComponents.putAll(componentPageMap);
        for(Entry<Integer, Map<Integer, InventoryMenuComponent>> e : componentPageMap.entrySet()) {
            int max = e.getValue()
                    .keySet()
                    .stream()
                    .mapToInt(i -> i)
                    .max()
                    .orElse(0);
            int rows = (max + ROWSIZE) / ROWSIZE;
            Inventory inventory = Bukkit.createInventory(this, rows * ROWSIZE, title);
            e.getValue().forEach((key, value) -> inventory.setItem(key, value.getDisplayItemWrapper().construct(slp)));
            inventories.put(e.getKey(), inventory);
        }
        setParents();
    }
    
    private TreeMap<Integer, Map<Integer, InventoryMenuComponent>> generateComponentPageMap(Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuComponent>> allComponents) {
        TreeMap<Integer, Map<Integer, InventoryMenuComponent>> pageMap = allComponents
                .entrySet()
                .stream()
                .filter((entry) -> (entry.getKey() >= 0 && entry.getValue().isVisible(slp)))
                .collect(Collectors.groupingBy(e -> e.getKey() / (e.getValue().getOverwritePageBehavior() ? MAX_PAGE_SIZE : pagesize),
                        TreeMap::new,
                        Collectors.toMap(
                                e -> e.getKey() % (e.getValue().getOverwritePageBehavior() ? MAX_PAGE_SIZE : pagesize), 
                                e -> e.getValue().construct(slp))));
        return pageMap;
    }
    
    private InventoryMenuItemTemplate createNextPageItem(int page) {
        return InventoryMenuAPI.item()
                .displayItem(new ItemStack(Material.DIAMOND_AXE, 1, (short)8))
                .displayName(ChatColor.GREEN + "Next page")
                .description("Click to go to the next page")
                .onClick(event -> {
                    this.open(page + 1);
                })
                .build();
    }
    
    private InventoryMenuItemTemplate createPreviousPageItem(int page) {
        return InventoryMenuAPI.item()
                .displayItem(new ItemStack(Material.DIAMOND_AXE, 1, (short)9))
                .displayName(ChatColor.RED + "Last page")
                .description("Click to go to the previous page")
                .onClick(event -> {
                    this.open(page - 1);
                })
                .build();
    }

    protected void addMenuControls() {
        if (InventoryMenuFlag.isSet(flags, InventoryMenuFlag.MENU_CONTROL)) {
            InventoryMenuComponent rootComp = getRoot();
            if (rootComp instanceof InventoryMenu) {
                if (getParent() != null) {
                    InventoryMenuItemTemplate goBackItem = InventoryMenuAPI.item()
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
                            .build();
                    allComponents.put(0, goBackItem);
                }
            }
        }
    }
    
    @Override
    public void selected(ClickType clickType) {
        //Directly opening submenu if it's the only option available
        if(this.isSet(InventoryMenuFlag.SKIP_SINGLE_SUBMENU) && currentComponents.size() == 1) {
            Map<Integer, InventoryMenuComponent> pageOne = currentComponents.get(0);
            if(pageOne != null) {
                if(pageOne.size() == 1) {
                    InventoryMenuComponent imc = pageOne.values().stream().findAny().orElse(null);
                    if(imc instanceof InventoryMenu) {
                        imc.selected(clickType);
                        return;
                    }
                }
            }
        }
        open();
    }
    
    public void open(int page) {
        Player player = slp.getPlayer();
        if (!this.hasAccess(slp)) {
            player.sendMessage(ChatColor.RED + "You are not allowed to open this InventoryMenu");
        } else {
            this.currentPage = page;
            Inventory i = inventories.get(page);
            if(i == null) {
                player.closeInventory();
            }
            else {
                player.openInventory(i);
            }
        }
    }

    public void open() {
        open(0);
    }

    public void close(Player player) {
        currentPage = 0;
        boolean anyContains = inventories.values()
                .stream()
                .peek(i -> i.getViewers().remove(player))
                .anyMatch(i -> i.getViewers().contains(player));
        if (anyContains) {
            player.closeInventory();
        }
    }

    protected TreeMap<Integer, Inventory> getInventories() {
        return inventories;
    }

    protected SLPlayer getSLP() {
        return slp;
    }

    protected Map<Integer, Map<Integer, InventoryMenuComponent>> getCurrentComponents() {
        return currentComponents;
    }

    protected int getCurrentPage() {
        return currentPage;
    }
    
    public abstract void selectItem(int index, ClickType clickType);

    @Override
    public Inventory getInventory() {
        return inventories.get(currentPage);
    }
    
    public boolean isSet(InventoryMenuFlag flag) {
        return InventoryMenuFlag.isSet(flags, flag);
    }
    
    public int getFlags() {
        return flags;
    }

    public void update() {
        populateInventory();
        slp.openInventory(getInventory());
    }
}
