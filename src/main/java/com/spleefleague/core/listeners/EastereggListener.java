/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Jonas
 */
public class EastereggListener implements Listener {
    
    private static Listener instance;
    
    public static void init() {
        if(instance == null) {
            instance = new EastereggListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private EastereggListener() {
        
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block.getState() instanceof Sign) {
                Sign sign = (Sign)block.getState();
                if(sign.getLine(0).contains("[Easteregg]")) {
                    int num = Integer.parseInt(sign.getLine(1));
                    SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
                    if(!slp.getEastereggs().contains(num)) {
                        slp.sendMessage(Theme.SUCCESS + "You found easteregg " + num + "!");
                        slp.getEastereggs().add(num);
                    }
                    else {
                        slp.sendMessage(Theme.WARNING + "You already found this easteregg!");
                    }
                }
            }
        }
    }
}
