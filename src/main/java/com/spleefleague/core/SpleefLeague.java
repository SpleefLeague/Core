/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.core.listeners.*;
import com.spleefleague.core.portals.PortalManager;
import com.spleefleague.core.spawn.SpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.command.CommandLoader;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.queue.Challenge;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.core.utils.MultiBlockChangeUtil;
import com.spleefleague.core.utils.RuntimeCompiler;
import com.spleefleague.core.utils.Warp;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {
    
    private MongoClient mongo;
    private PlayerManager<SLPlayer> playerManager;
    private Location spawn;
    private CommandLoader commandLoader;
    private SpawnManager spawnManager;
    private PortalManager portalManager;
    
    public SpleefLeague() {
        super("[SpleefLeague]", ChatColor.GRAY + "[" + ChatColor.GOLD + "SpleefLeague" + ChatColor.GRAY + "]" + ChatColor.RESET);
    }
    
    @Override
    public void start() {
        //The order is important
        instance = this;
        Config.loadConfig();
        initMongo();
        Settings.loadSettings();
        applySettings();
        RuntimeCompiler.loadPermanentDebuggers();
        DatabaseConnection.initialize();
        Rank.init();
        commandLoader = CommandLoader.loadCommands(this, "com.spleefleague.core.command.commands");
        ChatManager.init();
        MultiBlockChangeUtil.init();
        FakeBlockHandler.init();
        VisibilityListener.init();
        EnvironmentListener.init();
        InfractionListener.init();
        InventoryMenuListener.init();
        AfkListener.init();
        InventoryMenuTemplateRepository.initTemplates();
        Warp.init();
        Challenge.init();
        playerManager = new PlayerManager<>(this, SLPlayer.class);
        portalManager = new PortalManager();
    }
    
    @Override
    public void stop() {
        playerManager.saveAll();
        mongo.close();
    }
    
    public BasicCommand getBasicCommand(String name) {
        return commandLoader.getCommand(name).getExecutor();
    }
    
    public void applySettings() {
        if(Settings.hasKey("default_world")) {
            String defaultWorld = Settings.getString("default_world");
            CorePlugin.DEFAULT_WORLD = Bukkit.getWorld(defaultWorld);
        }
        if(Settings.hasKey("spawn_new") && Settings.hasKey("spawn_max_players")) {
            List<SpawnManager.SpawnLocation> spawns = new ArrayList<>();
            ((List<List>) Settings.getList("spawn_new")).forEach((List list) -> spawns.add(new SpawnManager.SpawnLocation(Settings.getLocation(list))));
            spawnManager = new SpawnManager(spawns, Settings.getInteger("spawn_max_players"));
        } else if(Settings.hasKey("spawn")) {
            spawn = Settings.getLocation("spawn");
            if(spawn != null) {
                CorePlugin.DEFAULT_WORLD.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
            }
        }
        if (Settings.hasKey("max_players")) {
            setSlotSize(Settings.getInteger("max_players"));
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
    
    public void setSlotSize(int size) {
        try {
                String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
                Object playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer").getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
                Field maxplayers = playerlist.getClass().getSuperclass().getDeclaredField("maxPlayers");
                maxplayers.setAccessible(true);
                maxplayers.set(playerlist, size);
            } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException | InvocationTargetException ex) {
                Logger.getLogger(SpleefLeague.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public MongoClient getMongo() {
        return mongo;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public PortalManager getPortalManager() {
        return portalManager;
    }

    public PlayerManager<SLPlayer> getPlayerManager() {
        return playerManager;
    }
    
    public Location getSpawnLocation() {
        return spawn;
    }

    @Override
    public void syncSave(Player p) {
        SLPlayer slp = playerManager.get(p);
        if(slp != null) {
            EntityBuilder.save(slp, getPluginDB().getCollection("Players"));
        }
    }
    
    private static SpleefLeague instance;
    
    public static SpleefLeague getInstance() {
        return instance;
    }   
}
