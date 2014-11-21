/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core;

import com.mongodb.DB;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Jonas
 */
public abstract class CorePlugin extends JavaPlugin {
    
    private final String prefix;
    
    public CorePlugin(String prefix) {
        this.prefix = prefix;
    }
    
    public abstract DB getPluginDB();
    
    public String getPrefix() {
        return prefix;
    }
}
