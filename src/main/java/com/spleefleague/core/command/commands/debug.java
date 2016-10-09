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
import com.spleefleague.core.utils.Debugger;
import com.spleefleague.core.utils.RuntimeCompiler;
import com.spleefleague.core.utils.debugger.DebuggerStartResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas
 */
public class debug extends BasicCommand {

    public debug(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer kp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }

    @Override
    protected int runBlock(CommandSender cs, Command cmd, String[] args) {
        runConsole(cs, cmd, args);
        return 0;
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if (args.length == 0) {
            sendUsage(cs);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                Map<String, Debugger> running = RuntimeCompiler.getRunningDebuggers();
                if (running.isEmpty()) {
                    cs.sendMessage(ChatColor.RED + "No debuggers are running");
                    return;
                }
                cs.sendMessage(ChatColor.GRAY + "Running debuggers:");
                for (String s : running.keySet()) {
                    cs.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + s);
                }
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                    DebuggerStartResult result = RuntimeCompiler.debugFromHastebin(args[0], cs);
                    if (result == null) {
                        error(cs, "Failed starting debugger!");
                        return;
                    }
                    result.informDebuggerStarted(cs);
                });
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("stop")) {
                String n = RuntimeCompiler.stopDebugger(args[1]);
                if (n == null) {
                    error(cs, "Debugger not found!");
                } else {
                    success(cs, ChatColor.GRAY + "Successfully stopped debugger: " + ChatColor.GREEN + n);
                }
            } else if (args[0].toLowerCase().startsWith("-host=") && args[0].length() > "-host=".length()) {
                String host = args[0].substring("-host=".length());
                String key = args[1];
                Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                    DebuggerStartResult result = RuntimeCompiler.debugFromHastebin(host, key, cs);
                    if (result == null) {
                        error(cs, "Failed starting debugger!");
                        return;
                    }
                    result.informDebuggerStarted(cs);
                });
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
                    error(cs, "An error occurred!");
                }
            } else {
                sendUsage(cs);
            }
        }
    }
}
