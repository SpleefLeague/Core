package com.spleefleague.core.cosmetics;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class ArmorItem extends CItem {
    
    private final static ItemStack[] emptyArmor = new ItemStack[4];
    
    @Getter
    private final ItemStack[] armor;

    public ArmorItem(int id, String name, ItemStack[] armor, int costInCoins, int costInPremiumCredits) {
        super(id, name, CType.ARMOR, costInCoins, costInPremiumCredits);
        this.armor = armor;
    }

    @Override
    public void onSelecting(Player p) {
        p.getInventory().setArmorContents(armor);
    }

    @Override
    public void onRemoving(Player p) {
        p.getInventory().setArmorContents(emptyArmor);
    }

}
