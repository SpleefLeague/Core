package com.spleefleague.core.cosmetics.items;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.cosmetics.Collectibles;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> p.getInventory().setHelmet(hat));
    }

    @Override
    public void onRemoving(Player p) {
        handleRemoving(p);
    }
    
    static void handleRemoving(Player p) {
        p.getInventory().setHelmet(null);
        Collectibles col = SpleefLeague.getInstance().getPlayerManager().get(p).getCollectibles();
        if(col.isActive(CType.ARMOR)) {
            ArmorItem ai = (ArmorItem) col.getActiveItems().stream().filter(item -> item.getType() == CType.ARMOR).findAny().get();
            Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> p.getInventory().setHelmet(ai.getArmor()[3]));
        }
    }

}
