/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Jonas Balsfulland
 */
public interface Debugger {

    void debug();
    
    interface Stoppable {
        void stop();
    }
    
    interface CommandExecutor {
        void onCommand(CommandSender cs, String[] args);
    }
}