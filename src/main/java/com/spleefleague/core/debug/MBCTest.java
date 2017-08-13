/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.debug;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.utils.Debugger;
import com.spleefleague.core.utils.fakeblock.MultiBlockChangeUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author jonas
 */
public class MBCTest implements Debugger{
    
    public void debug(CommandSender cs, SpleefLeague instance) {
        Player p = (Player)cs;
        Location center = p.getLocation().add(0, 10, 0);
        Location low = center.clone().add(-2, -2, -2);
        Location high = center.clone().add(2, 2, 2);
        MultiBlockChangeUtil.changeBlocks(low, high, Material.SNOW, p);
    }
}
