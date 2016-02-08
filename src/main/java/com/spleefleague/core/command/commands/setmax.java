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
import com.spleefleague.core.io.Settings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class setmax extends BasicCommand {

    public setmax(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer kp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length == 1) {
            int i = getInteger(args[0]);
            if(i > 0) {
                SpleefLeague.getInstance().setSlotSize(i);
                Settings.set("max_players", i);
                success(cs, "Slot size has been changed to " + i + "!");
            }
            else {
                error(cs, args[0] + " is not a valid number.");
            }
        }
        else {
            sendUsage(cs);
        }
    }
    
    public static int getInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }
}
