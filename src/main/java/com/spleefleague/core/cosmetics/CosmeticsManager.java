package com.spleefleague.core.cosmetics;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
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
    
    private final static Map<Integer, CItem> ITEMS = new HashMap<>();
    private final static Map<CType, List<CItem>> PER_TYPES = new HashMap<>();
    
    static {
        for(CType type : CType.values())
            PER_TYPES.put(type, new ArrayList<>());
    }
    
    public static CItem getItem(int id) {
        return ITEMS.get(id);
    }
    
    public static List<CItem> getItems(CType type) {
        return PER_TYPES.get(type);
    }
    
    private static void add(CItem item) {
        ITEMS.put(item.getId(), item);
        PER_TYPES.get(item.getType()).add(item);
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
        add(new StatusEffectItem(id++, "&6Health boost", PotionEffectType.HEALTH_BOOST, 5, 1000, 2));
        add(new StatusEffectItem(id++, "&6Water breathing", PotionEffectType.WATER_BREATHING, 1, 1000, 2));
        add(new StatusEffectItem(id++, "&6&lSpeed II", PotionEffectType.SPEED, 2, 2000, 4));
        add(new StatusEffectItem(id++, "&6&lNight vision", PotionEffectType.NIGHT_VISION, 1, 2000, 4));
        
        add(new CommonHatItem(id++, new SimpleItemStack(Material.DIRT, "&7Dirt block hat"), 500, 1));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.SPONGE, "&7Sponge hat"), 500, 1));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.REDSTONE_LAMP_OFF, "&7Redstone lamp hat",
                Lists.newArrayList("&7&oOff, because you are not smart")), 500, 1));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.PUMPKIN, "&6Pumpkin"), 1000, 2));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.THIN_GLASS, "&6Glass pane hat"), 1000, 2));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.GLASS, "&6Glass block hat"), 1000, 2));
        add(new RainbowHatItem(id++, 2000, 4));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.BARRIER, "&6&lCountry flag (wip)"), 2000, 4).availability(Availability.DISABLED));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.BEACON, "&6&lBeacon hat"), 2000, 4));
        add(new CommonHatItem(id++, new SimpleItemStack(Material.MELON_BLOCK, "&c&lMelon hat"), 999999, 50));
        
        add(new ParticleEffectItem(id++, "&7Explosion effect", EnumWrappers.Particle.EXPLOSION_NORMAL, 500, 1));
        add(new ParticleEffectItem(id++, "&7Large smoke effect", EnumWrappers.Particle.SMOKE_LARGE, 500, 1));
        add(new ParticleEffectItem(id++, "&7Red dust effect", EnumWrappers.Particle.REDSTONE, 500, 1));
        add(new ParticleEffectItem(id++, "&6Angry villager effect", EnumWrappers.Particle.VILLAGER_ANGRY, 1000, 2));
        add(new ParticleEffectItem(id++, "&6Happy villager effect", EnumWrappers.Particle.VILLAGER_HAPPY, 1000, 2));
        add(new ParticleEffectItem(id++, "&6Spell effect", EnumWrappers.Particle.SPELL, 1000, 2));
        add(new ParticleEffectItem(id++, "&6&lNote effect", EnumWrappers.Particle.NOTE, 2000, 4));
        add(new ParticleEffectItem(id++, "&6&lHeart effect", EnumWrappers.Particle.HEART, 2000, 4));
        add(new ParticleEffectItem(id++, "&6&lSnow shovel effect", EnumWrappers.Particle.SNOW_SHOVEL, 2000, 4));
        
        add(new HeadHatItem(id++, "OneSpoone",
                "MjNiN2RjZmE3NTk2NjNkNjdhOGFmZTc5ZWQwMWExMjI2NzY2N2FiYjM3ZmU2MWRiODM5ZTIwM2NiMzEwMmIifX19",
                500, 1, Lists.newArrayList("&7Admin of da SpleefLeague")));
        add(new HeadHatItem(id++, "Jecth",
                "MmRmMWQyY2RlNzI1MDc0OTYxNWE4NmRlZGE1MmRkNzA1ZjE5YTE5MWIwMzYzODZiMTQ3YzM4Y2Y2Yzk1NCJ9fX0=",
                500, 1, Lists.newArrayList("&7Admin of da SpleefLeague")));
        add(new HeadHatItem(id++, "Sinsie",
                "OWFlNGNhZDE1MmZjMWYzYThkOTMyNTg3NzY3N2I0OWNiNDhjNGE1NmMyNWMyNTZlM2M1YWM0YzY0YmUxYTg3MyJ9fX0=",
                500, 1, Lists.newArrayList("&7Admin of da SpleefLeague")));
        add(new HeadHatItem(id++, "Blaezon",
                "OWFlNGNhZDE1MmZjMWYzYThkOTMyNTg3NzY3N2I0OWNiNDhjNGE1NmMyNWMyNTZlM2M1YWM0YzY0YmUxYTg3MyJ9fX0=",
                500, 1, Lists.newArrayList(
                        "&7Admin & Lead Builder",
                        "&7of da SpleefLeague"
                )));
        add(new HeadHatItem(id++, "Vatonage",
                "YWE4ZWM5Y2IxOTQwMWIzNDdlYTA4NzYxNjU0NzE5ODkxM2Q0YjM5OTYxNmM0YTczOWUxZTFhMDU0MDZhNzMifX19",
                500, 1, Lists.newArrayList(
                        "&7Council & Builder",
                        "&7of the SpleefLeague"
                )));
        add(new HeadHatItem(id++, "Worme",
                "YTk1MWRkOTI3OWExYTdkMDkyMTkyNzU0ZDRmOWNlNGFlZGM5YTY0MWNhZGI2NzY3ZjU2NzViYWRlYTkyZGVlIn19fQ==",
                500, 1, Lists.newArrayList(
                        "&7Council & Builder",
                        "&7of the SpleefLeague"
                )));
        add(new HeadHatItem(id++, "Joba",
                "Y2ZhYzY0NmU0NGIyNzU3MjZhOWFlZTE3YmY2ZTZjZmU4NjYxN2VkOTQ5ZTRhNDhhMmUxZDkxMWU4NjljYTRmOCJ9fX0=",
                500, 1, Lists.newArrayList("&7Retired Owner of the SpleefLeague")));
        add(new HeadHatItem(id++, "Thalan10",
                "ZWUyMzVkY2NmZDk0YWI2NGYwZWE0MjE0MjRlNjRiOWFhODc4NzM2YTg3MjJlNDkzZjUwMmY1MmRkYWE0YiJ9fX0=",
                500, 1, Lists.newArrayList("&7SL 0.0 owner")));
        add(new HeadHatItem(id++, "PxlPanda",
                "ZmQzOTk3ZTQ0ZmVmNmQ1NDllMjE3YjMyMjI3YzZjYzE1ODgxMWE4OTQ4YmE5Nzg1ZDM5MjY0NzhmYTM3Zjg0In19fQ==",
                500, 1, Lists.newArrayList("&7SL 1.0 & MultiCube owner")));
    }
    
}
