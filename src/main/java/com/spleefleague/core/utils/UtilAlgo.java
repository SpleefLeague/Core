package com.spleefleague.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class UtilAlgo {

    private final static Random r = new Random();
    
    public static int r() {
        return r.nextInt();
    }
    
    public static int r(int bound) {
        return r.nextInt(bound);
    }

    public static JSONObject serialize(ItemStack is) {
        JSONObject json = new JSONObject();
        if(is == null) {
            json.put("id", 0);
            json.put("data", 0);
            json.put("amount", 0);
            return json;
        }
        json.put("id", is.getTypeId());
        json.put("data", is.getDurability());
        json.put("amount", is.getAmount());
        ItemMeta im = is.getItemMeta();
        if(im == null)
            return json;
        if(im.hasDisplayName())
            json.put("name", im.getDisplayName());
        List<String> list = im.getLore();
        if(list == null)
            list = new ArrayList<>();
        if(!list.isEmpty()) {
            JSONArray lore = new JSONArray();
            list.forEach(lore::add);
            json.put("lore", lore);
        }
        Map<Enchantment, Integer> enchants = im.getEnchants();
        if(enchants != null && !enchants.isEmpty()) {
            JSONArray enchs = new JSONArray();
            enchants.forEach((e, l) -> {
                JSONObject enchantment = new JSONObject();
                enchantment.put("id", e.getId());
                enchantment.put("lvl", l);
                enchs.add(enchantment);
            });
            json.put("enchantments", enchs);
        }
        if(im instanceof SkullMeta) {
            SkullMeta sm = (SkullMeta) im;
            if(sm.getOwner() != null)
                json.put("head-owner", sm.getOwner());
        }
        return json;
    }
    
    public static ItemStack deserialize(JSONObject json) {
        int id = (int) (long) json.get("id");
        short data = (short) (long) json.get("data");
        int amount = (int) (long) json.get("amount");
        ItemStack is = new ItemStack(id, amount, data);
        if(id == 0)
            return is;
        ItemMeta im = is.getItemMeta();
        if(json.containsKey("name"))
            im.setDisplayName((String) json.get("name"));
        if(json.containsKey("lore")) {
            List<String> lore = new ArrayList<>();
            JSONArray array = (JSONArray) json.get("lore");
            for(int i = 0; i < array.size(); ++i)
                lore.add((String) json.get(i));
            im.setLore(lore);
        }
        if(json.containsKey("enchantments")) {
            JSONArray array = (JSONArray) json.get("enchantments");
            for(int i = 0; i < array.size(); ++i) {
                JSONObject ench = (JSONObject) array.get(i);
                im.addEnchant(Enchantment.getById((int) (long) ench.get("id")), (int) (long) ench.get("lvl"), true);
            }
        }
        if(json.containsKey("head-owner")) {
            SkullMeta sm = (SkullMeta) im;
            sm.setOwner((String) json.get("head-owner"));
        }
        is.setItemMeta(im);
        return is;
    }

    public static Location strToLoc(String s) {
        String[] spl = s.split(" ");
        World w = Bukkit.getWorld(spl[0]);
        if(w == null)
            Bukkit.createWorld(new WorldCreator(spl[0]));
        w = Bukkit.getWorld(spl[0]);
        double x = Double.parseDouble(spl[1]);
        double y = Double.parseDouble(spl[2]);
        double z = Double.parseDouble(spl[3]);
        float yaw = Float.parseFloat(spl[4]);
        float pitch = Float.parseFloat(spl[5]);
        Location l = new Location(w, x, y, z);
        l.setYaw(yaw);
        l.setPitch(pitch);
        return l;
    }
    
    public static String locToStr(Location l) {
        StringBuilder sb = new StringBuilder();
        sb.append(l.getWorld().getName()).append(" ")
                .append(l.getX()).append(" ")
                .append(l.getY()).append(" ")
                .append(l.getZ()).append(" ")
                .append(l.getYaw()).append(" ")
                .append(l.getPitch());
        return sb.toString();
    }
    
}
