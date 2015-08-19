package com.spleefleague.core.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.GeneralPlayerLoadedEvent;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.player.GeneralPlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;

public class DBListener implements Listener{
	private static Listener instance;
	    
    public static void init() {
        if(instance == null) {
            instance = new DBListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    
    @EventHandler
    public void onGeneralPlayerLoaded(GeneralPlayerLoadedEvent event){
    	GeneralPlayer gp = event.getGeneralPlayer();
    	
    	if(gp instanceof SLPlayer){
    		SLPlayer slp = (SLPlayer) gp;
    		
    		if(slp.getRank().hasPermission(Rank.DEVELOPER)){
    			// InventoryMenuTemplateRepository.showModMenu(slp.getPlayer());
    			ItemStack is = InventoryMenuTemplateRepository.devMenu.getDisplayItemStackFor(slp.getPlayer());
    			slp.getPlayer().getInventory().setItem(6, is);
    		}
    	}
    	
    }
}
