package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.events.BattleEndEvent;
import com.spleefleague.core.events.BattleStartEvent;
import com.spleefleague.core.events.PlayerEndedSpectatingEvent;
import com.spleefleague.core.events.PlayerStartedSpectatingEvent;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.queue.Battle;
import java.util.Collection;
import java.util.HashSet;
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
    
    private final static PlayerManager<SLPlayer> pm = SpleefLeague.getInstance().getPlayerManager();
    
    //Is being initialized in CosmeticsManager
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new CosmeticsListener(), SpleefLeague.getInstance());
    }
    
    private CosmeticsListener() {}
    
    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent e) {
        SLPlayer slp = pm.get(e.getPlayer());
        slp.getCollectibles().unapply(slp);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        SLPlayer slp = pm.get(p);
        if(e.getSlotType() == SlotType.ARMOR)
            if(slp.getCollectibles().isActive(CType.ARMOR) || slp.getCollectibles().isActive(CType.HAT))
                e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBattleStart(BattleStartEvent e) {
        reapplyCollectiblesForAllPlayersOfDaBattle(e.getBattle());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBattleEnd(BattleEndEvent e) {
        reapplyCollectiblesForAllPlayersOfDaBattle(e.getBattle());
    }
    
    @EventHandler
    public void onPlayerStartedSpectating(PlayerStartedSpectatingEvent e) {
        pm.get(e.getPlayer()).reapplyCollectibles();
    }
    
    @EventHandler
    public void onPlayerEndedSpectating(PlayerEndedSpectatingEvent e) {
        pm.get(e.getPlayer()).reapplyCollectibles();
    }
    
    private void reapplyCollectiblesForAllPlayersOfDaBattle(Battle battle) {
        Collection<Player> players = new HashSet<>();
        players.addAll(battle.getPlayers());
        players.addAll(battle.getSpectators());
        players.stream().map(pm::get).forEach(SLPlayer::reapplyCollectibles);
    }
    
}
