package com.spleefleague.core.cosmetics;

import com.spleefleague.core.listeners.CosmeticsListener;
import com.spleefleague.core.utils.SimpleItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        add(new ArmorItem(id++, "&7Leather armor", new ItemStack[]{
            new SimpleItemStack(Material.LEATHER_BOOTS, "&7Leather boots"),
            new SimpleItemStack(Material.LEATHER_LEGGINGS, "&7Leather leggings"),
            new SimpleItemStack(Material.LEATHER_CHESTPLATE, "&7Leather chestplate"),
            new SimpleItemStack(Material.LEATHER_HELMET, "&7Leather helmet")
        }, 500, 1));
        add(new ArmorItem(id++, "&6&lGold armor", new ItemStack[]{
            new SimpleItemStack(Material.GOLD_BOOTS, "&6&lGold boots"),
            new SimpleItemStack(Material.GOLD_LEGGINGS, "&6&lGold leggings"),
            new SimpleItemStack(Material.GOLD_CHESTPLATE, "&6&lGold chestplate"),
            new SimpleItemStack(Material.GOLD_HELMET, "&6&lGold helmet")
        }, 2000, 4));
        add(new ArmorItem(id++, "&b&lDiamond armor", new ItemStack[]{
            new SimpleItemStack(Material.DIAMOND_BOOTS, "&b&lDiamond boots"),
            new SimpleItemStack(Material.DIAMOND_LEGGINGS, "&b&lDiamond leggings"),
            new SimpleItemStack(Material.DIAMOND_CHESTPLATE, "&b&lDiamond chestplate"),
            new SimpleItemStack(Material.DIAMOND_HELMET, "&b&lDiamond helmet")
        }, 5000, 10));
        add(new ArmorItem(id++, "&bAqua leather armor &4&l(TEST)", new ItemStack[]{
            new SimpleItemStack(Material.LEATHER_BOOTS, "&bAqua leather boots", Color.AQUA),
            new SimpleItemStack(Material.LEATHER_LEGGINGS, "&bAqua leather leggings", Color.AQUA),
            new SimpleItemStack(Material.LEATHER_CHESTPLATE, "&bAqua leather chestplate", Color.AQUA),
            new SimpleItemStack(Material.LEATHER_HELMET, "&bAqua leather helmet", Color.AQUA)
        }, 500, 1));
        add(new ArmorItem(id++, "&5&lEnchanted diamond armor &4&l(TEST)", new ItemStack[]{
            new SimpleItemStack(Material.DIAMOND_BOOTS, "&5&lDiamond boots", Enchantment.PROTECTION_ENVIRONMENTAL, 10),
            new SimpleItemStack(Material.DIAMOND_LEGGINGS, "&5&lDiamond leggings", Enchantment.PROTECTION_ENVIRONMENTAL, 10),
            new SimpleItemStack(Material.DIAMOND_CHESTPLATE, "&5&lDiamond chestplate", Enchantment.PROTECTION_ENVIRONMENTAL, 10),
            new SimpleItemStack(Material.DIAMOND_HELMET, "&5&lDiamond helmet", Enchantment.PROTECTION_ENVIRONMENTAL, 10)
        }, 500, 1));
    }
    
}
