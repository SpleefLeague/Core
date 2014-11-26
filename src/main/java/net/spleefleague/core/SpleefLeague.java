/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core;

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
import org.bukkit.ChatColor;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {
    
    private MongoClient mongo;
    private PlayerManager<SLPlayer> playerManager;

    public SpleefLeague() {
        super("[SpleefLeague]", ChatColor.GRAY + "[" + ChatColor.GOLD + "SpleefLeague" + ChatColor.GRAY + "]" + ChatColor.RESET);
    }
    
    @Override
    public void onEnable() {
        instance = this;
        Config.loadConfig();
        try {
            mongo = new MongoClient(Config.DB_HOST, Config.DB_PORT);
            this.mongo.getMongoOptions().autoConnectRetry = true;
            this.mongo.getMongoOptions().connectionsPerHost = 10;
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
        CommandLoader.loadCommands(this, "net.spleefleague.core.command.commands");
        playerManager = new PlayerManager<>(getPluginDB(), SLPlayer.class);
        ChatListener.init();
        EnvironmentListener.init();
        InfractionListener.init();
    }
    
    @Override
    public void onDisable() {
        mongo.close();
    }
    
    @Override
    public DB getPluginDB() {
        return mongo.getDB("SpleefLeague");
    }
    
    public PlayerManager<SLPlayer> getPlayerManager() {
        return playerManager;
    }
    
    private static SpleefLeague instance;
    
    public static SpleefLeague getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        try {
            MongoClient mongo = new MongoClient("127.0.0.1", 27017);
            mongo.getMongoOptions().autoConnectRetry = true;
            mongo.getMongoOptions().connectionsPerHost = 10;
            for(String dbname : mongo.getDatabaseNames()) {
                System.out.println(dbname);
            }
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
