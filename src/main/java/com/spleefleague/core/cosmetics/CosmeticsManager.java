package com.spleefleague.core.cosmetics;

import com.spleefleague.core.cosmetics.items.*;
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
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CosmeticsManager {
    
    private final static Map<Integer, CItem> items = new HashMap<>();
    private final static Map<CType, List<CItem>> perTypes = new HashMap<>();
    
    static {
        for(CType type : CType.values())
            perTypes.put(type, new ArrayList<>());
    }
    
    public static CItem getItem(int id) {
        return items.get(id);
    }
    
    public static List<CItem> getItems(CType type) {
        return perTypes.get(type);
    }
    
    private static void add(CItem item) {
        items.put(item.getId(), item);
        perTypes.get(item.getType()).add(item);
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
        add(new StatusEffectItem(id++, "&7Nausea", PotionEffectType.CONFUSION, 1, 500, 1));
        add(new StatusEffectItem(id++, "&7Blindness", PotionEffectType.BLINDNESS, 1, 500, 1));
        add(new StatusEffectItem(id++, "&7Slowness", PotionEffectType.CONFUSION, 1, 500, 1));
        add(new StatusEffectItem(id++, "&6Health Boost", PotionEffectType.HEALTH_BOOST, 5, 1000, 2));
        add(new StatusEffectItem(id++, "&6Water Breathing", PotionEffectType.WATER_BREATHING, 1, 1000, 2));
        add(new StatusEffectItem(id++, "&6&lSpeed II", PotionEffectType.SPEED, 2, 2000, 4));
        add(new StatusEffectItem(id++, "&6&lNight Vision", PotionEffectType.NIGHT_VISION, 1, 2000, 4));
    }
    
}
