package com.spleefleague.core.utils.inventorymenu;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Tuple;
import com.spleefleague.core.utils.collections.MapUtil;
import com.spleefleague.core.utils.inventorymenu.dialog.InventoryMenuDialog;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractInventoryMenu<C extends InventoryMenuComponent> extends SelectableInventoryMenuComponent implements InventoryHolder {

    public static final int ROWSIZE = 9;
    public static final int COLUMNSIZE = 6;
    private static final int PAGE_NAVIGATION_SIZE = ROWSIZE * 2;
    private final int MAX_PAGE_SIZE = ROWSIZE * COLUMNSIZE;

    private final TreeMap<Integer, Inventory> inventories;
    private final Map<Integer, Tuple<Supplier<InventoryMenuComponentTemplate<? extends C>>, InventoryMenuComponentAlignment>> standardTemplates;
    private final Map<Integer, Supplier<InventoryMenuComponentTemplate<? extends C>>> staticTemplates;
    private final Function<SelectableInventoryMenuComponent, C> componentMapper;
    private final String title;
    private final SLPlayer slp;
    private final Map<Integer, Map<Integer, C>> renderedComponents;
    private final int pagesize = ROWSIZE * COLUMNSIZE - PAGE_NAVIGATION_SIZE;
    private final int flags;
    private int currentPage = 0;
    
    protected AbstractInventoryMenu(
            AbstractInventoryMenu parent,
            ItemStackWrapper displayItem, 
            String title, 
            Map<Integer, Tuple<Supplier<InventoryMenuComponentTemplate<? extends C>>, InventoryMenuComponentAlignment>> components, 
            Map<Integer, Supplier<InventoryMenuComponentTemplate<? extends C>>> staticComponents, 
            Function<SelectableInventoryMenuComponent, C> componentMapper,
            Function<SLPlayer, Boolean> accessController, 
            Function<SLPlayer, Boolean> visibilityController, 
            SLPlayer slp, 
            int flags) {
        super(parent, displayItem, visibilityController, accessController, InventoryMenuFlag.isSet(flags, InventoryMenuFlag.IGNORE_PAGE_OVERFLOW));
        this.flags = flags;
        this.slp = slp;
        this.standardTemplates = components;
        this.staticTemplates = staticComponents;
        this.inventories = new TreeMap<>();
        this.title = title;
        this.renderedComponents = new HashMap<>();
        this.componentMapper = componentMapper;
        renderInventory();
    }

    public SLPlayer getOwner() {
        return slp;
    }

    protected void renderInventory() {
        int highestDefined = 0;
        Predicate<C> displayEmptyMenu = (c) -> {
            if(this.isSet(InventoryMenuFlag.HIDE_EMPTY_SUBMENU)) {
                if(c instanceof AbstractInventoryMenu) {
                    return !((AbstractInventoryMenu) c).isEmpty();
                }
                else if(c instanceof InventoryMenuDialog) {
                    return !((InventoryMenuDialog) c).isEmpty();
                }
            }
            return true;
        };
        Map<Integer, Tuple<C, InventoryMenuComponentAlignment>> visibleStandardComponents = this.standardTemplates
                .entrySet()
                .stream()
                .map(e -> new Tuple<>(e.getKey(), new Tuple<InventoryMenuComponentTemplate<? extends C>, InventoryMenuComponentAlignment>(e.getValue().x.get(), e.getValue().y)))
                .filter(e -> e.y.x.isVisible(slp)) //Avoid constructing invisible components
                .map(e -> new Tuple<>(e.x, new Tuple<>(e.y.x.construct(this, slp), e.y.y)))
                .filter(e -> displayEmptyMenu.test(e.y.x))
                .collect(Collectors.toMap(e -> e.x, e -> e.y));
        Map<Integer, C> visibleStaticComponents = this.staticTemplates
                .entrySet()
                .stream()
                .map(e -> new Tuple<>(e.getKey(), e.getValue().get()))
                .filter(e -> e.y.isVisible(slp))
                .map(e -> new Tuple<>(e.x, e.y.construct(this, slp)))
                .filter(e -> displayEmptyMenu.test(e.y))
                .collect(Collectors.toMap(e -> e.x, e -> e.y));
        for(Entry<Integer, ?> e : visibleStandardComponents.entrySet()) {
            highestDefined = Math.max(highestDefined, e.getKey());
        }
        boolean multiPage = Math.max(visibleStandardComponents.size(), highestDefined) > pagesize;
        final TreeMap<Integer, Map<Integer, C>> visibleComponentPageMap = generateComponentPageMap(visibleStandardComponents);
        SortedMap<InventoryMenuComponentAlignment, Queue<C>> fillupQueueMap = visibleStandardComponents
                .entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getKey(), e1.getKey()))
                .filter(e -> e.getKey() < 0)
                .map(e -> new Tuple<>(e.getValue().x, e.getValue().y))
                .collect(Collectors.groupingBy(t -> t.y, () -> new TreeMap<>(), Collectors.mapping(t -> t.x, Collectors.toCollection(() -> new LinkedList<>()))));
        fillupQueueMap.forEach((align, fillupQueue) -> {
            for(int page = 0; !fillupQueue.isEmpty(); page++) {
                Map<Integer, C> slots = visibleComponentPageMap.getOrDefault(page, new HashMap<>());
                if(slots.size() > pagesize) continue;
                fillPage(slots, fillupQueue, align);
                visibleComponentPageMap.put(page, slots);
            }
        });
        //Compressing menu page structure to avoid gaps ([2,3,6,8,9] -> [0,1,2,3,4])
        visibleComponentPageMap.put(-1, null);
        TreeMap<Integer, Map<Integer, C>> compressedComponentPageMap = MapUtil.compress(visibleComponentPageMap);
        compressedComponentPageMap.remove(-1);
        compressedComponentPageMap.values()
                .stream()
                .forEach(m -> {
                    m.putAll(visibleStaticComponents);
                    
                });
        
        //Adding page navigation
        if(multiPage) {
            int max = compressedComponentPageMap.lastKey();
            for (int page = 0; page <= max; page++) {
                Map<Integer, C> slots = compressedComponentPageMap.get(page);
                if(page > 0) {
                    C lastPage = componentMapper.apply(createPreviousPageItem(page).construct(this, slp));
                    slots.put(MAX_PAGE_SIZE - 9, lastPage);
                }
                if(page < max) {
                    C nextPage = componentMapper.apply(createNextPageItem(page).construct(this, slp));
                    slots.put(MAX_PAGE_SIZE - 1, nextPage);
                }
            }
        }
        //Creating Bukkit inventories
        inventories.clear();
        renderedComponents.clear();
        renderedComponents.putAll(compressedComponentPageMap);
        for(Entry<Integer, Map<Integer, C>> e : compressedComponentPageMap.entrySet()) {
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
    }
    
    private void fillPage(Map<Integer, C> slots, Queue<C> components, InventoryMenuComponentAlignment alignment) {
        int items = slots.size() + components.size();
        int pageHeight = (items + ROWSIZE - 1) / ROWSIZE;
        pageHeight = Math.min(pageHeight, this.pagesize / ROWSIZE);
        OptionalInt slotOpt = OptionalInt.of(alignment.getStart(pageHeight));
        while(!components.isEmpty() && slotOpt.isPresent()) {
            int slot = slotOpt.getAsInt();
            if(!slots.containsKey(slot)) {
                C c = components.poll();
                slots.put(slot, c);

            }
            slotOpt = alignment.next(slot, pageHeight);
        }
    }
    
    //Creates a map with each component that has a defined position on its (provisional) page
    private TreeMap<Integer, Map<Integer, C>> generateComponentPageMap(Map<Integer, Tuple<C, InventoryMenuComponentAlignment>> visibleComponents) {
        TreeMap<Integer, Map<Integer, C>> pageMap = visibleComponents
                .entrySet()
                .stream()
                .filter((entry) -> (entry.getKey() >= 0))
                .collect(Collectors.groupingBy(e -> e.getKey() / (e.getValue().x.getOverwritePageBehavior() ? MAX_PAGE_SIZE : pagesize),
                        TreeMap::new,
                        Collectors.toMap(
                                e -> e.getKey() % (e.getValue().x.getOverwritePageBehavior() ? MAX_PAGE_SIZE : pagesize), 
                                e -> e.getValue().x)));
//        
//        TreeMap<Integer, Map<Integer, C>> controlMap = getMenuControls()
//                .entrySet()
//                .stream()
//                .filter((entry) -> (entry.getKey() >= 0))
//                .collect(Collectors.groupingBy(e -> e.getKey() / (e.getValue().getOverwritePageBehavior() ? MAX_PAGE_SIZE : pagesize),
//                        TreeMap::new,
//                        Collectors.toMap(
//                                e -> e.getKey() % (e.getValue().getOverwritePageBehavior() ? MAX_PAGE_SIZE : pagesize), 
//                                e -> componentMapper.apply(e.getValue().construct(this, slp)))));
//        Integer fp = pageMap.isEmpty() ? null : pageMap.firstKey();
//        Integer fc = controlMap.isEmpty() ? null : controlMap.firstKey();
//        while(fc != null) {
//            if(fc.equals(fp)) {
//                Map<Integer, C> mp = pageMap.get(fp);
//                Map<Integer, C> mc = controlMap.get(fc);
//                for(Entry<Integer, C> e : mc.entrySet()) {
//                    mp.put(e.getKey(), e.getValue());
//                }
//                fp = pageMap.higherKey(fp);
//                fc = controlMap.higherKey(fc);
//            }
//            else if(fp == null) {
//                pageMap.put(fc, controlMap.get(fc));
//                fc = controlMap.higherKey(fc);
//            }
//            else if(fc < fp) {
//                fc = controlMap.higherKey(fc);
//            }
//            else if(fp < fc) {
//                fp = controlMap.higherKey(fp);
//            }
//        }
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

    protected Map<Integer, Tuple<InventoryMenuItemTemplate, InventoryMenuComponentAlignment>> getMenuControls() {
        Map<Integer, Tuple<InventoryMenuItemTemplate, InventoryMenuComponentAlignment>> components = new HashMap<>();
        if (InventoryMenuFlag.isSet(flags, InventoryMenuFlag.MENU_CONTROL)) {
            InventoryMenuComponent rootComp = getRoot();
            if (rootComp instanceof InventoryMenu) {
                if (getParent() != null) {
                    components.put(0, new Tuple<>(InventoryMenuAPI.item()
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
                            .build(), InventoryMenuComponentAlignment.REVERSE));
                    return components;
                }
            }
        }
        return components;
    }
    
    @Override
    public void selected(ClickType clickType) {
        //Directly opening submenu if it's the only option available
        if(this.isSet(InventoryMenuFlag.SKIP_SINGLE_SUBMENU) && renderedComponents.size() == 1) {
            Map<Integer, C> pageOne = renderedComponents.get(0);
            if(pageOne != null) {
                if(pageOne.size() == 1) {
                    C imc = pageOne.values().stream().findAny().orElse(null);
                    if(imc instanceof InventoryMenu) {
                        ((InventoryMenu) imc).selected(clickType);
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
                if(isSet(InventoryMenuFlag.CLOSE_EMPTY_SUBMENU)) {    
                    player.closeInventory();
                }
            }
            else {
                player.openInventory(i);
            }
        }
    }
    
    public boolean isEmpty() {
        return inventories.isEmpty();
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

    protected Map<Integer, Map<Integer, C>> getCurrentComponents() {
        return renderedComponents;
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
        renderInventory();
        slp.openInventory(getInventory());
    }
}
