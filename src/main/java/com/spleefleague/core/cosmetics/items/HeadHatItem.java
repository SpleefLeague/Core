package com.spleefleague.core.cosmetics.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.utils.SimpleItemStack;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class HeadHatItem extends CItem {
    
    private final ItemStack hat;

    public HeadHatItem(int id, String nickname, String data, int costInCoins, int costInPremiumCredits, List<String> description) {
        super(id, nickname = "&7Head of " + nickname, CType.HAT, description, costInCoins, costInPremiumCredits);
        this.hat = createSkull(nickname, data);
    }
    
    public ItemStack getHat() {
        return hat;
    }

    @Override
    public void onSelecting(Player p) {
        p.getInventory().setHelmet(hat);
    }

    @Override
    public void onRemoving(Player p) {
        p.getInventory().setHelmet(null);
    }
    
    private static ItemStack createSkull(String name, String urlToFormat) {
        String url = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + urlToFormat;
        ItemStack head = new SimpleItemStack(Material.SKULL_ITEM, name, (short) 3);
        if(url.isEmpty())
            return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        }catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

}
