/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.plugin;

import com.mongodb.DB;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;
import net.spleefleague.core.player.GeneralPlayer;
import net.spleefleague.core.player.PlayerManager;
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
        DEFAULT_WORLD = Bukkit.getWorlds().get(0);
        plugins.add(this);
        start();
    }
    
    @Override
    public final void onDisable() {
        plugins.remove(this);
        stop();
    }
    
    public void start(){}
    public void stop(){}
    
    public abstract DB getPluginDB();
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getChatPrefix() {
        return chatPrefix;
    }
    
    public static Collection<CorePlugin> getAll() {
        return plugins;
    }
    
    public static Collection<CoreGame> getCoreGames() {
        Collection<CoreGame> coreGames = new HashSet<>();
        for(CorePlugin plugin : getAll()) {
            if(plugin instanceof CoreGame) {
                coreGames.add((CoreGame)plugin);
            }
        }
        return coreGames;
    }
    
    public static Collection<QueueableCoreGame> getQueueableCoreGames() {
        Collection<QueueableCoreGame> coreGames = new HashSet<>();
        for(CorePlugin plugin : getAll()) {
            if(plugin instanceof CoreGame) {
                coreGames.add((QueueableCoreGame)plugin);
            }
        }
        return coreGames;
    }
}
