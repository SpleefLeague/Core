/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core;

import com.mongodb.DB;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Jonas
 */
public abstract class CorePlugin extends JavaPlugin {
    
    private final String prefix, chatPrefix;
    public static Logger LOG = Logger.getLogger("Minecraft");
    public static World DEFAULT_WORLD = Bukkit.getWorlds().get(0);
    
    public CorePlugin(String prefix, String chatPrefix) {
        this.prefix = prefix;
        this.chatPrefix = chatPrefix;
    }
    
    public abstract DB getPluginDB();
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getChatPrefix() {
        return chatPrefix;
    }
}
