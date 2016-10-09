package com.spleefleague.core.utils.debugger;

import com.spleefleague.core.utils.Debugger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class DebuggerStartResult {

    private Debugger debugger;
    private String name;

    public DebuggerStartResult(Debugger debugger, String name) {
        this.debugger = debugger;
        this.name = name;
    }

    public void informDebuggerStarted(CommandSender cs) {
        if (!(debugger instanceof Debugger.Stoppable || debugger instanceof Debugger.CommandExecutor || debugger instanceof Listener)) {
            cs.sendMessage(ChatColor.GRAY + "Running debugger class: " + ChatColor.GREEN + name);
            return;
        }
        cs.sendMessage(ChatColor.GRAY + "Started debugger class with id: " + ChatColor.GREEN + name);
        if (debugger instanceof Debugger.CommandExecutor) {
            cs.sendMessage(ChatColor.GRAY + "run cmd: " + ChatColor.BLUE + "/rd command/cmd " + ChatColor.GREEN + name +
                           ChatColor.BLUE + " <command>");
        }
        cs.sendMessage(ChatColor.GRAY + "stop: " + ChatColor.BLUE + "/rd stop " + ChatColor.GREEN + name);
    }

    public String getName() {
        return this.name;
    }

    public Debugger getDebugger() {
        return this.debugger;
    }

}
