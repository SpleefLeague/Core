/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class invsee extends BasicCommand {

    public invsee(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length >= 1) {
            Player pl;
            if ((pl = Bukkit.getPlayerExact(args[0])) != null) {
                p.openInventory(pl.getInventory());
                success(p, "Moo.");
            } else {
                error(p, "The player \"" + args[0] + "\" is not online!");
            }
        } else {
            sendUsage(p);
        }
    }
}
