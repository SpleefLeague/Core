/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core;

import com.spleefleague.core.listeners.InfractionListener;
import com.spleefleague.core.listeners.EnvironmentListener;
import com.spleefleague.core.listeners.VanishListener;
import com.spleefleague.core.listeners.EastereggListener;
import com.spleefleague.core.listeners.ChatListener;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.plugin.CorePlugin;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.command.CommandLoader;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.core.utils.RuntimeCompiler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {
    
    private MongoClient mongo;
    private PlayerManager<SLPlayer> playerManager;
    private Location spawn;
    private CommandLoader commandLoader;
    
    public SpleefLeague() {
        super("[SpleefLeague]", ChatColor.GRAY + "[" + ChatColor.GOLD + "SpleefLeague" + ChatColor.GRAY + "]" + ChatColor.RESET);
    }
    
    @Override
    public void start() {
        instance = this;
        Config.loadConfig();
        initMongo();
        Settings.loadSettings();
        applySettings();
        RuntimeCompiler.loadPermanentDebuggers();
        commandLoader = CommandLoader.loadCommands(this, "net.spleefleague.core.command.commands");
        DatabaseConnection.initialize();
        Rank.init();
        playerManager = new PlayerManager<>(this, SLPlayer.class);
        ChatManager.registerChannel(new ChatChannel("DEFAULT", "Normal chat", Rank.DEFAULT, true));
        ChatManager.registerChannel(new ChatChannel("STAFF", "Staff chat", Rank.MODERATOR, true));
        ChatListener.init();
        EnvironmentListener.init();
        InfractionListener.init();
        //ItemMenuListener.init();
        VanishListener.init();
        EastereggListener.init();
    }
    
    @Override
    public void stop() {
        playerManager.saveAll();
        mongo.close();
    }
    
    public BasicCommand getBasicCommand(String name) {
        return commandLoader.getCommand(name).getExecutor();
    }
    
    private void applySettings() {
        if(Settings.hasKey("default_world")) {
            String defaultWorld = Settings.getString("default_world");
            CorePlugin.DEFAULT_WORLD = Bukkit.getWorld(defaultWorld);
        }
        if(Settings.hasKey("spawn")) {
            spawn = Settings.getLocation("spawn");
            if(spawn != null) {
                CorePlugin.DEFAULT_WORLD.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
            }
        }
        if (Settings.hasKey("max_players")) {
            try {
                String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
                Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
                Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
                maxplayers.setAccessible(true);
                maxplayers.set(playerlist, Settings.getInteger("max_players"));
            } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException | InvocationTargetException ex) {
                Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void initMongo() {
        List<MongoCredential> credentials = Config.getCredentials();
        try {
            ServerAddress address = new ServerAddress(Config.DB_HOST, Config.DB_PORT);
            mongo = new MongoClient(address, credentials);
        } catch (Exception ex) {
            Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public MongoDatabase getPluginDB() {
        return mongo.getDatabase("SpleefLeague");
    }
    
    public MongoClient getMongo() {
        return mongo;
    }
    
    public PlayerManager<SLPlayer> getPlayerManager() {
        return playerManager;
    }
    
    public Location getSpawnLocation() {
        return spawn;
    }
    
    private static SpleefLeague instance;
    
    public static SpleefLeague getInstance() {
        return instance;
    }   
}
