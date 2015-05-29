/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.plugin;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import java.util.Collection;
import java.util.HashSet;
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
    public static final Logger LOG = Logger.getLogger("Minecraft");
    public static World DEFAULT_WORLD;
    public static HashSet<CorePlugin> plugins = new HashSet<>();
    
    public CorePlugin(String prefix, String chatPrefix) {
        this.prefix = prefix;
        this.chatPrefix = chatPrefix;
    }
    
    @Override
    public final void onEnable() {
        plugins.add(this);
        DEFAULT_WORLD = Bukkit.getWorlds().get(0);
        start();
    }
    
    @Override
    public final void onDisable() {
        plugins.remove(this);
        stop();
    }
    
    public void start(){}
    public void stop(){}
    
    public abstract MongoDatabase getPluginDB();
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getChatPrefix() {
        return chatPrefix;
    }
    
    public void log(String message) {
        System.out.println(prefix + " " + message);
    }
    
    public static Collection<CorePlugin> getAll() {
        return plugins;
    }
}
