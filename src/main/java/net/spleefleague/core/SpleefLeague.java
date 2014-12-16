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
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.command.CommandLoader;
import net.spleefleague.core.listeners.ChatListener;
import net.spleefleague.core.listeners.EnvironmentListener;
import net.spleefleague.core.listeners.InfractionListener;
import net.spleefleague.core.player.PlayerManager;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.Tutorial;
import net.spleefleague.core.utils.DatabaseConnection;
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
        instance = this;
        Config.loadConfig();
        initMongo();
        protocolManager = ProtocolLibrary.getProtocolManager();
        CommandLoader.loadCommands(this, "net.spleefleague.core.command.commands");
        DatabaseConnection.initialize();
        playerManager = new PlayerManager<>(this, SLPlayer.class);
        ChatListener.init();
        EnvironmentListener.init();
        InfractionListener.init();
        Tutorial.initialize();
    }
    
    private void initMongo() {
        HashMap<String, String> credentials = Config.getCredentials();
        try {
            ServerAddress address = new ServerAddress(Config.DB_HOST, Config.DB_PORT);
            mongo = new MongoClient(address);
            this.mongo.getMongoOptions().autoConnectRetry = true;
            this.mongo.getMongoOptions().connectionsPerHost = 10;
            for(String db : credentials.keySet()) {
                boolean successful = mongo.getDB(db).authenticate("Plugin", credentials.get(db).toCharArray());
                if(!successful) {
                    System.out.println(getPrefix() + " Authentication error: " + db);
                }
                else {
                    System.out.println(getPrefix() + " Authentication successful: " + db);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
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
