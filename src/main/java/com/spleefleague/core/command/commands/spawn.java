/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.plugin.GamePlugin;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class spawn extends BasicCommand{

    public spawn(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(slp.getState() == PlayerState.INGAME) {
            error(p, "You are currently ingame!");
        }
        else if(slp.getState() == PlayerState.SPECTATING) {
            GamePlugin.unspectateGlobal(p);
        }
        p.teleport(SpleefLeague.getInstance().getSpawnLocation());
    }
}
