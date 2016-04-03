/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.DatabaseConnection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class setrank extends BasicCommand {

    public setrank(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if (args.length == 2) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if (player != null) {
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(player.getUniqueId());
                if (slp != null) {
                    Rank rank;
                    try {
                        rank = Rank.valueOf(args[1].toUpperCase());
                    } catch (Exception e) {
                        rank = null;
                    }
                    if (rank != null) {
                        slp.setRank(rank);
                        success(cs, "Rank has been set.");
                    } else {
                        error(cs, "The rank " + args[1] + " does not exist!");
                    }
                } else {
                    setRankOffline(cs, args[0], args[1]);
                }
            } else {
                setRankOffline(cs, args[0], args[1]);
            }
        } else {
            sendUsage(cs);
        }
    }

    private void setRankOffline(CommandSender cs, String name, String r) {
        Rank rank = Rank.valueOf(r);
        if (rank == null) {
            error(cs, "The rank " + r + " does not exist!");
        } else {
            DatabaseConnection.updateFields(SpleefLeague.getInstance().getPluginDB().getCollection("Players"), new Document("username", name), new Document("rank", rank.getName()));
            success(cs, "Attempting rank update for offline player.");
        }
    }
}
