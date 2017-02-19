package com.spleefleague.core.cosmetics.items;

import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.utils.SafePlayerTask;
import com.spleefleague.core.utils.SimpleItemStack;
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
        SafePlayerTask.call(p, player -> {
            ItemStack helmet = player.getInventory().getHelmet();
            player.getInventory().setArmorContents(armor);
            if (helmet != null)
                player.getInventory().setHelmet(helmet);
        });
    }

    @Override
    public void onRemoving(Player p) {
        SafePlayerTask.call(p, player -> {
            ItemStack helmet = player.getInventory().getHelmet();
            player.getInventory().setArmorContents(EMPTY_ARMOR);
            if(!SimpleItemStack.areEqualByNames(helmet, armor[3]))
                player.getInventory().setHelmet(helmet);
        });
    }

}
