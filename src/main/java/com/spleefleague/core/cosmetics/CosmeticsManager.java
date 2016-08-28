package com.spleefleague.core.cosmetics;

import com.spleefleague.core.listeners.CosmeticsListener;
import com.spleefleague.core.utils.SimpleItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CosmeticsManager {
    
    private final static Map<Integer, CItem> items = new HashMap<>();
    private final static Map<CType, List<CItem>> perTypes = new HashMap<>();
    
    public static CItem getItem(int id) {
        return items.get(id);
    }
    
    public static List<CItem> getItems(CType type) {
        return perTypes.get(type);
    }
    
    private static void add(CItem item) {
        items.put(item.getId(), item);
        CType type = item.getType();
        List<CItem> items = perTypes.get(type);
        if(items == null) {
            items = new ArrayList<>();
            perTypes.put(type, items);
        }
        items.add(item);
    }

    public static void init() {
        CosmeticsListener.init();
        int id = 1;
        add(new ArmorItem(id++, "Leather Armor", new ItemStack[]{
            new SimpleItemStack(Material.LEATHER_BOOTS, "Leather boots"),
            new SimpleItemStack(Material.LEATHER_LEGGINGS, "Leather leggings"),
            new SimpleItemStack(Material.LEATHER_CHESTPLATE, "Leather chestplate"),
            new SimpleItemStack(Material.LEATHER_HELMET, "Leather helmet")
        }, 500, 1));
        add(new ArmorItem(id++, "Gold Armor", new ItemStack[]{
            new SimpleItemStack(Material.GOLD_BOOTS, "Gold boots"),
            new SimpleItemStack(Material.GOLD_LEGGINGS, "Gold leggings"),
            new SimpleItemStack(Material.GOLD_CHESTPLATE, "Gold chestplate"),
            new SimpleItemStack(Material.GOLD_HELMET, "Gold helmet")
        }, 2000, 4));
        add(new ArmorItem(id++, "Diamond Armor", new ItemStack[]{
            new SimpleItemStack(Material.DIAMOND_BOOTS, "Diamond boots"),
            new SimpleItemStack(Material.DIAMOND_LEGGINGS, "Diamond leggings"),
            new SimpleItemStack(Material.DIAMOND_CHESTPLATE, "Diamond chestplate"),
            new SimpleItemStack(Material.DIAMOND_HELMET, "Diamond helmet")
        }, 5000, 10));
    }
    
}
