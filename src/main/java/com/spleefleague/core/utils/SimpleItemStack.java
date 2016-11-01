package com.spleefleague.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 *
 * @author Константин
 */
public class SimpleItemStack extends ItemStack {
    
    public SimpleItemStack(Material m, String name, String desc, Object... datas) {
        this(m, 1, name, desc, datas);
    }
    
    public SimpleItemStack(Material m, String name, List<String> desc, Object... datas) {
        this(m, 1, name, desc, datas);
    }
    
    public SimpleItemStack(PotionType pt, int level, boolean extended_duration, boolean splash, int amount, String name) {
        this(Material.POTION, amount, name, "", getPotionType(pt, level, extended_duration, splash));
    }
    
    public SimpleItemStack(Material m, int amount, String name, List<String> desc, Object... datas) {
        super(m, amount);
        ItemMeta im = getItemMeta();
        im.setDisplayName(UtilChat.c("&e%s", name));
        setItemMeta(im);
        if(desc != null && !desc.isEmpty()) {
            im.setLore(desc.stream().map(UtilChat::c).collect(Collectors.toList()));
            setItemMeta(im);
        }
        if(datas == null || datas.length == 0) return;
        for(int i = 0; i < datas.length; ++i) {
            Object data = datas[i];
            if(data instanceof Color) {
                try {
                    LeatherArmorMeta lam = (LeatherArmorMeta) im;
                    lam.setColor((Color) data);
                    setItemMeta(lam);
                }catch(Exception ex) {}
            }else if(data instanceof Enchantment && datas[i + 1] instanceof Integer) {
                addUnsafeEnchantment((Enchantment) data, (Integer) datas[i + 1]);
                ++i;
            }else if(data instanceof Integer)
                this.setAmount((Integer) data);
            else if(data instanceof Short)
                this.setDurability((short) data);
        }
    }
    
    public SimpleItemStack(Material m, int amount, String name, String desc, Object... datas) {
        this(m, amount, name, descStringToList(desc), datas);
    }
    
    public SimpleItemStack(Material m, int amount, String name) {
        this(m, amount, name, "", (Object[]) null);
    }
    
    public SimpleItemStack(Material m, String name) {
        this(m, name, "");
    }
    
    public SimpleItemStack(Material m, String name, List<String> desc) {
        this(m, name, desc, (Object[]) null);
    }
    
    public SimpleItemStack(Material m, String name, String desc) {
        this(m, name, desc, (Object[]) null);
    }
    
    public SimpleItemStack(Material m, String name, Object... objects) {
        this(m, name, "", objects);
    }
    
    public SimpleItemStack applyFlags(ItemFlag... flags) {
        ItemMeta im = getItemMeta();
        im.addItemFlags(flags);
        setItemMeta(im);
        return this;
    }
    
    public SimpleItemStack setUnbreakable(boolean value) {
        ItemMeta im = getItemMeta();
        im.spigot().setUnbreakable(value);
        setItemMeta(im);
        return this;
    }
    
    private static short getPotionType(PotionType pt, int level, boolean extended_duration, boolean splash) {
        Potion pot = new Potion(pt);
        pot.setLevel(level);
        pot.setSplash(splash);
        if(extended_duration)
            try {
                pot.setHasExtendedDuration(extended_duration);
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        return pot.toItemStack(1).getDurability();
    }
    
    private static List<String> descStringToList(String desc) {
        List<String> list = new ArrayList<>();
        if(desc == null || desc.isEmpty())
            return list;
        list.addAll(Arrays.asList(desc.split("\\|")));
        return list;
    }
    
    public static boolean areEqualByNames(ItemStack is1, ItemStack is2) {
        if(is1 == null)
            return is2 == null;
        if(is2 == null)
            return false;
        ItemMeta im1 = is1.getItemMeta(), im2 = is2.getItemMeta();
        if(im1 == null)
            return im2 == null;
        if(im2 == null)
            return false;
        String name1 = im1.getDisplayName(), name2 = im2.getDisplayName();
        if(name1 == null)
            return name2 == null;
        if(name2 == null)
            return false;
        return name1.equals(name2);
    }
    
}
