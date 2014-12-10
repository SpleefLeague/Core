/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core;

import net.spleefleague.core.plugin.CorePlugin;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.command.CommandLoader;
import net.spleefleague.core.listeners.ChatListener;
import net.spleefleague.core.listeners.EnvironmentListener;
import net.spleefleague.core.listeners.InfractionListener;
import net.spleefleague.core.player.PlayerManager;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.Tutorial;
import org.bukkit.ChatColor;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {
    
    private MongoClient mongo;
    private PlayerManager<SLPlayer> playerManager;
    private ProtocolManager protocolManager;
    
    public SpleefLeague() {
        super("[SpleefLeague]", ChatColor.GRAY + "[" + ChatColor.GOLD + "SpleefLeague" + ChatColor.GRAY + "]" + ChatColor.RESET);
    }
    
    @Override
    public void start() {
        super.onEnable();
        instance = this;
        Config.loadConfig();
        try {
            mongo = new MongoClient(Config.DB_HOST, Config.DB_PORT);
            this.mongo.getMongoOptions().autoConnectRetry = true;
            this.mongo.getMongoOptions().connectionsPerHost = 10;
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
        protocolManager = ProtocolLibrary.getProtocolManager();
        CommandLoader.loadCommands(this, "net.spleefleague.core.command.commands");
        playerManager = new PlayerManager<>(this, SLPlayer.class);
        ChatListener.init();
        EnvironmentListener.init();
        InfractionListener.init();
        Tutorial.initialize();
    }
    
    @Override
    public void stop() {
        mongo.close();
        for(Tutorial tutorial : Tutorial.getTutorials()) {
            tutorial.end(false);
        }
    }
    
    @Override
    public DB getPluginDB() {
        return mongo.getDB("SpleefLeague");
    }
    
    public MongoClient getMongo() {
        return mongo;
    }
    
    public PlayerManager<SLPlayer> getPlayerManager() {
        return playerManager;
    }
    
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    
    private static SpleefLeague instance;
    
    public static SpleefLeague getInstance() {
        return instance;
    }
    
}
