package com.spleefleague.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.core.io.connections.ConnectionClient;
import com.spleefleague.core.listeners.*;
import com.spleefleague.core.portals.PortalManager;
import com.spleefleague.core.spawn.SpawnManager;
import org.bson.Document;
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
import com.spleefleague.core.cosmetics.CosmeticsManager;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.queue.Challenge;
import com.spleefleague.core.utils.AutoBroadcaster;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.core.utils.DynamicCommandManager;
import com.spleefleague.core.utils.MultiBlockChangeUtil;
import com.spleefleague.core.utils.RuntimeCompiler;
import com.spleefleague.core.utils.Warp;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {

    public static final String BROADCAST_FORMAT = ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Broadcast"
            + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "%s";

    private MongoClient mongo;
    private Rank minimumJoinRank;
    private List<Rank> extraJoinRanks;
    private PlayerManager<SLPlayer> playerManager;
    private Location spawn;
    private ConnectionClient connectionClient;
    private CommandLoader commandLoader;
    private SpawnManager spawnManager;
    private PortalManager portalManager;
    private DynamicCommandManager dynamicCommandManager;

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
        loadJoinSettings();
        commandLoader = CommandLoader.loadCommands(this, "com.spleefleague.core.command.commands");
        ChatManager.init();
        MultiBlockChangeUtil.init();
        FakeBlockHandler.init();
        VisibilityListener.init();
        EnvironmentListener.init();
        InfractionListener.init();
        InventoryMenuListener.init();
        CosmeticsManager.init();
        ConnectionListener.init();
        AfkListener.init();
        InventoryMenuTemplateRepository.initTemplates();
        Warp.init();
        Challenge.init();
        AutoBroadcaster.init();
        playerManager = new PlayerManager<>(this, SLPlayer.class);
        portalManager = new PortalManager();
        connectionClient = new ConnectionClient();
        dynamicCommandManager = new DynamicCommandManager(this);
    }

    @Override
    public void stop() {
        playerManager.saveAll();
        connectionClient.stop();
        mongo.close();
    }

    public BasicCommand getBasicCommand(String name) {
        return commandLoader.getCommand(name).getExecutor();
    }

    public void applySettings() {
        if (Settings.hasKey("default_world")) {
            String defaultWorld = Settings.getString("default_world");
            CorePlugin.DEFAULT_WORLD = Bukkit.getWorld(defaultWorld);
        }
        if (Settings.hasKey("spawn_new") && Settings.hasKey("spawn_max_players")) {
            List<SpawnManager.SpawnLocation> spawns = new ArrayList<>();
            ((List<List>) Settings.getList("spawn_new")).forEach((List list) -> spawns.add(new SpawnManager.SpawnLocation(Settings.getLocation(list))));
            spawnManager = new SpawnManager(spawns, Settings.getInteger("spawn_max_players"));
        } else if (Settings.hasKey("spawn")) {
            spawn = Settings.getLocation("spawn");
            if (spawn != null) {
                CorePlugin.DEFAULT_WORLD.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
            }
        }
        if (Settings.hasKey("max_players")) {
            setSlotSize(Settings.getInteger("max_players"));
        }
        if(Settings.hasKey("game_rules")) {
            Document document = Settings.getDocument("game_rules");
            document.forEach((String key, Object object) -> CorePlugin.DEFAULT_WORLD.setGameRuleValue(key, object.toString()));
        }
    }

    private void loadJoinSettings() {
        minimumJoinRank = (Config.hasKey("minimum_join_rank") ? Config.getRank("minimum_join_rank") : Rank.DEFAULT);
        List<Rank> result = new ArrayList<>();
        if(Config.hasKey("extra_join_ranks")) {
            Config.getList("extra_join_ranks").forEach((Object object) -> {
                Rank rank;
                try {
                    rank = Rank.valueOf(object.toString().toUpperCase());
                } catch (Exception e) {
                    return;
                }
                result.add(rank);
            });
        }
        extraJoinRanks = result;
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

    public Rank getMinimumJoinRank() {
        return minimumJoinRank;
    }

    public List<Rank> getExtraJoinRanks() {
        return extraJoinRanks;
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

    public ConnectionClient getConnectionClient() {
        return connectionClient;
    }

    // Use the SpawnManager instead.
    @Deprecated
    public Location getSpawnLocation() {
        return spawn;
    }
    
    public DynamicCommandManager getDynamicCommandManager() {
        return this.dynamicCommandManager;
    }

    @Override
    public void syncSave(Player p) {
        SLPlayer slp = playerManager.get(p);
        if (slp != null) {
            EntityBuilder.save(slp, getPluginDB().getCollection("Players"));
        }
    }

    private static SpleefLeague instance;

    public static SpleefLeague getInstance() {
        return instance;
    }
}
