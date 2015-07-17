/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import net.spleefleague.core.utils.Debugger;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

/**
 *
 * @author Jonas
 */
public class BreakPaintings implements Debugger, Listener{

    @Override
    public void debug() {
    }
    
    @EventHandler
    public void onFrameBrake(HangingBreakEvent e) {
        e.setCancelled(true);
        if(e.getEntity() instanceof Player) {
            e.setCancelled(((Player)e.getEntity()).getGameMode() != GameMode.CREATIVE);
        }
    }
}
