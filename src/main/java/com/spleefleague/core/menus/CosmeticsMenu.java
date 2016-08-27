package com.spleefleague.core.menus;

import com.google.common.collect.Lists;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.cosmetics.CosmeticsManager;
import com.spleefleague.core.menus.cosmetics.ArmorInventory;
import com.spleefleague.core.menus.cosmetics.CInventory;
import com.spleefleague.core.utils.rines.RButton;
import com.spleefleague.core.utils.rines.RInventory;
import com.spleefleague.core.utils.rines.RInventoryManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CosmeticsMenu extends RInventory {

    @Getter
    private final static CosmeticsMenu instance = new CosmeticsMenu();

    private CosmeticsMenu() {
        super("Cosmetics", 1);
        addItem(new RButton(Material.LEATHER_CHESTPLATE, "Armor", Lists.newArrayList(
                "&7Collect and wear all",
                "&7the armor sets.",
                "&7All sets are active",
                "&7both in the world and",
                "&7during the game."
        )){
            @Override
            public void onClick(Player p, int slot) {
                RInventoryManager.openInventory(p, new ArmorInventory(p, 1, getMaxPages(CType.ARMOR)));
            }
        }, 1, 1);
    }
    
    private int getMaxPages(CType type) {
        int size = CosmeticsManager.getItems(type).size();
        int pages = 1 + size / CInventory.getItemsPerPage();
        if(size % CInventory.getItemsPerPage() == 0)
            --pages;
        return pages;
    }
    
}
