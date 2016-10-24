package com.spleefleague.core.cosmetics.items;

import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CommonHatItem extends CItem {
    
    private final ItemStack hat;

    public CommonHatItem(int id, ItemStack hat, int costInCoins, int costInPremiumCredits) {
        super(id, hat.getItemMeta().getDisplayName(), CType.HAT, costInCoins, costInPremiumCredits);
        this.hat = hat;
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

}
