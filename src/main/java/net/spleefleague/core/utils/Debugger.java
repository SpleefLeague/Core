/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Wouter
 */
public interface Debugger {

    public void debug();
    
    public static interface Stoppable {
        public void stop();
    }
    
    public static interface CommandExecutor {
        public void onCommand(CommandSender cs, String[] cmdArgs);
    }
}