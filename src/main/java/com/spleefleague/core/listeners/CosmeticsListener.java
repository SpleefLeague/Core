package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CosmeticsListener implements Listener {
    
    //Is being initialized in CosmeticsManager
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new CosmeticsListener(), SpleefLeague.getInstance());
    }
    
    private CosmeticsListener() {}

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
        if(e.getSlotType() == SlotType.ARMOR)
            if(slp.getCollectibles().isActive(CType.ARMOR))
                e.setCancelled(true);
    }
    
}
