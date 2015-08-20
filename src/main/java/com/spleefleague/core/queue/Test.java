/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Debugger;
import org.bukkit.Bukkit;

/**
 *
 * @author Jonas
 */
public class Test implements Debugger{

    @Override
    public void debug() {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(Bukkit.getPlayer("Preloa"));
        System.out.println(slp.getRank());
        System.out.println(Rank.valueOf("SPECIAL"));
    }   
}
