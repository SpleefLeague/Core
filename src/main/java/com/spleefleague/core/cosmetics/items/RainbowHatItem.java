package com.spleefleague.core.cosmetics.items;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.utils.SimpleItemStack;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class RainbowHatItem extends CItem {
    
    private final static String ITEM_NAME = "&6&lRainbow hat";
    private final static Set<Player> AFFECTED_PLAYERS = new HashSet<>();
    private final static long INTERVAL = 10l;
    private final static ItemStack HAT = new SimpleItemStack(Material.STAINED_GLASS, ITEM_NAME);
    
    static {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefLeague.getInstance(), () -> {
            HAT.setDurability((short) ((HAT.getDurability() + 1) % 16));
            AFFECTED_PLAYERS.stream().map(Player::getInventory).forEach(pi -> pi.setHelmet(HAT));
        }, INTERVAL, INTERVAL);
    }

    public RainbowHatItem(int id, int costInCoins, int costInPremiumCredits) {
        super(id, ITEM_NAME, CType.HAT, costInCoins, costInPremiumCredits);
    }
    
    public ItemStack getHat() {
        return HAT;
    }

    @Override
    public void onSelecting(Player p) {
        AFFECTED_PLAYERS.add(p);
    }

    @Override
    public void onRemoving(Player p) {
        AFFECTED_PLAYERS.remove(p);
        CommonHatItem.handleRemoving(p);
    }

}
