package com.spleefleague.core.cosmetics.items;

import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class ArmorItem extends CItem {
    
    private final static ItemStack[] EMPTY_ARMOR = new ItemStack[4];
    
    private final ItemStack[] armor;

    public ArmorItem(int id, String name, ItemStack[] armor, int costInCoins, int costInPremiumCredits) {
        super(id, name, CType.ARMOR, costInCoins, costInPremiumCredits);
        this.armor = armor;
    }
    
    public ItemStack[] getArmor() {
        return armor;
    }

    @Override
    public void onSelecting(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        p.getInventory().setArmorContents(armor);
        if(helmet != null)
            p.getInventory().setHelmet(helmet);
    }

    @Override
    public void onRemoving(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        p.getInventory().setArmorContents(EMPTY_ARMOR);
        if(helmet != armor[3])
            p.getInventory().setHelmet(helmet);
    }

}
