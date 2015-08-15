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
public class endgame extends BasicCommand {

    public endgame(CorePlugin plugin, String name, String usage) {
        super(SpleefLeague.getInstance(), name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(slp.getState() == PlayerState.INGAME) {
            if(args.length == 0) {
                GamePlugin.requestEndgameGlobal(p);
            }
            else {
                sendUsage(p);
            }
        }
        else {
            error(p, "You are not ingame!");
        }
    }
}
