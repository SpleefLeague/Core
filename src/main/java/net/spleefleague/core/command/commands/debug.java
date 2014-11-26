/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.CorePlugin;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.utils.RuntimeCompiler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class debug extends BasicCommand {

    public debug(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer kp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if (args.length == 0) {
            sendUsage(cs);
        } else {
            if (args.length == 1) {
                String[] clzl = RuntimeCompiler.debugFromHastebin(args[0]);
                if (clzl == null) {
                    error(cs, "Failed starting debugger!");
                    return;
                }
                String name = clzl[0];
                if(clzl.length == 1) {
                    success(cs, ChatColor.GRAY + "Running debugger class: " + ChatColor.GREEN + name);
                }
                else if (clzl.length == 2) {
                    success(cs, ChatColor.GRAY + "Started debugger class with id: " + ChatColor.GREEN + name);
                    success(cs, ChatColor.GRAY + "run cmd: " + ChatColor.BLUE + "/rd command/cmd " + ChatColor.GREEN + name + ChatColor.BLUE + " <command>");
                    success(cs, ChatColor.GRAY + "stop: " + ChatColor.BLUE + "/rd stop " + ChatColor.GREEN + name);
                } 
                else if(clzl.length == 3) {
                    success(cs, ChatColor.GRAY + "Started debugger class with id: " + ChatColor.GREEN + name);
                    success(cs, ChatColor.GRAY + "stop: " + ChatColor.BLUE + "/rd stop " + ChatColor.GREEN + name);
                }
                else if(clzl.length == 4) {
                    success(cs, ChatColor.GRAY + "Started debugger class with id: " + ChatColor.GREEN + name);
                    success(cs, ChatColor.GRAY + "run cmd: " + ChatColor.BLUE + "/rd command/cmd " + ChatColor.GREEN + name + ChatColor.BLUE + " <command>");
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("stop")) {
                    String n = RuntimeCompiler.stopDebugger(args[1]);
                    if (n == null) {
                        error(cs, "Debugger not found!");
                    } else {
                        success(cs, ChatColor.GRAY + "Succesfully stopped debugger: " + ChatColor.GREEN + n);
                    }
                } else {
                    sendUsage(cs);
                }
            } else if (args.length > 2) {
                if (args[0].equalsIgnoreCase("command") || args[0].equalsIgnoreCase("cmd")) {
                    try {
                        if (!RuntimeCompiler.runDebuggerCommand(args[1], cs, Arrays.copyOfRange(args, 2, args.length))) {
                            error(cs, "Debugger not found!");
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(debug.class.getName()).log(Level.SEVERE, null, ex);
                        error(cs, "An error occured!");
                    }
                } else {
                    sendUsage(cs);
                }
            }
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
}
