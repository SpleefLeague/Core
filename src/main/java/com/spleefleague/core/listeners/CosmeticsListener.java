package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.events.BattleEndEvent;
import com.spleefleague.core.events.BattleStartEvent;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.queue.Battle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;

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
    
    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(e.getPlayer());
        slp.getCollectibles().unapply(slp);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
        if(e.getSlotType() == SlotType.ARMOR)
            if(slp.getCollectibles().isActive(CType.ARMOR) || slp.getCollectibles().isActive(CType.HAT))
                e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBattleStart(BattleStartEvent e) {
        reapplyCollectiblesForAllActivePlayersOfDaBattle(e.getBattle());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBattleEnd(BattleEndEvent e) {
        reapplyCollectiblesForAllActivePlayersOfDaBattle(e.getBattle());
    }
    
    private void reapplyCollectiblesForAllActivePlayersOfDaBattle(Battle battle) {
        PlayerManager<SLPlayer> pm = SpleefLeague.getInstance().getPlayerManager();
        for(Object objectplayer : battle.getActivePlayers()) {
            SLPlayer slp = pm.get((Player) objectplayer);
            slp.getCollectibles().reapply(slp);
        }
    }
    
}
