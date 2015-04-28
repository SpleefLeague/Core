/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.GeneralPlayer;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.plugin.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class cancel extends BasicCommand {

    public cancel(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        Player tp = Bukkit.getPlayerExact(args[0]);
        if (tp != null) {
            GeneralPlayer gp = SpleefLeague.getInstance().getPlayerManager().get(tp);
            if (gp != null) {
                if (GamePlugin.isIngameAll(tp)) {
                    GamePlugin.cancelAll(tp);
                    success(cs, "The battle will be cancelled.");
                } 
                else {
                    error(cs, "The player " + ChatColor.WHITE + tp.getName() + ChatColor.RED + " is not playing!");
                }
            } 
            else {
                error(cs, "The player " + ChatColor.WHITE + args[0] + ChatColor.RED + " is not initialized yet!");
            }
        } 
        else {
            error(cs, "The player " + ChatColor.WHITE + args[0] + ChatColor.RED + " is not online!");
        }
    }
}
