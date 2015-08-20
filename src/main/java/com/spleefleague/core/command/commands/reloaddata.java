/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class reloaddata extends BasicCommand{

    public reloaddata(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length == 0) {
            Bukkit.reload();
        }
        else if(args[0].equalsIgnoreCase("ranks")) {
            Rank.init();
            for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                slp.setRank(Rank.valueOf(slp.getRank().getName()));
            }
            success(cs, "Reloaded " + Rank.values().length + " ranks!");
        }
    }
}
