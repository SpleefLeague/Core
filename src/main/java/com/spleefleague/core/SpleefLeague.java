package com.spleefleague.core;

import ch.qos.logback.classic.LoggerContext;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.spleefleague.core.utils.debugger.RuntimeCompiler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.io.connections.ConnectionClient;
import com.spleefleague.core.io.typeconverters.LocationConverter;
import com.spleefleague.core.listeners.*;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.packets.PingCalculationAdapter;
import com.spleefleague.core.player.DBPlayerManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.plugin.PlayerHandling;
import com.spleefleague.core.portals.PortalManager;
import com.spleefleague.core.spawn.SpawnManager;
import com.spleefleague.core.spawn.SpawnManager.SpawnLocation;
import com.spleefleague.core.utils.*;
import com.spleefleague.core.utils.debugger.DebuggerHostManager;
import com.spleefleague.entitybuilder.EntityBuilder;
import com.spleefleague.virtualworld.VirtualWorld;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin implements PlayerHandling {

    public static final String BROADCAST_FORMAT = ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Broadcast"
            + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "%s";

    private MongoClient mongo;
    private Rank minimumJoinRank;
    private List<Rank> extraJoinRanks;
    private DBPlayerManager<SLPlayer> playerManager;
    private AutoBroadcaster autoBroadcaster;
    private Location spawn;
    private ConnectionClient connectionClient;
    private SpawnManager spawnManager;
    private PortalManager portalManager;
    private DebuggerHostManager debuggerHostManager;
    private ServerType serverType;
    
    public SpleefLeague() {
        super(ChatColor.GRAY + "[" + ChatColor.GOLD + "SpleefLeague" + ChatColor.GRAY + "]" + ChatColor.RESET);
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
        ChatManager.init();
        EnvironmentListener.init();
        InfractionListener.init();
        InventoryMenuListener.init();
        ConnectionListener.init();
        AfkListener.init();
        PlayerUtil.init();
        Warp.init();
        getProtocolManager().addPacketListener(new PingCalculationAdapter(20));
        autoBroadcaster = new AutoBroadcaster(getMongo().getDatabase("SpleefLeague").getCollection("AutoBroadcaster"));
        playerManager = new DBPlayerManager<>(this, SLPlayer.class);
        portalManager = new PortalManager();
        connectionClient = new ConnectionClient();
        debuggerHostManager = new DebuggerHostManager();
        InventoryMenuTemplateRepository.initTemplates();
        Settings.getList("debugger_hosts").ifPresent(hosts -> debuggerHostManager.reloadAll(hosts));
    }

    @Override
    public void stop() {
        playerManager.saveAll();
        autoBroadcaster.stopTask();
        connectionClient.stop();
        mongo.close();
    }

    public void applySettings() {
        //Main world
        String defaultWorld = Settings.getString("default_world").orElse("world");
        CorePlugin.DEFAULT_WORLD = Bukkit.getWorld(defaultWorld);
        //Spawn handling
        LocationConverter lc = new LocationConverter();
        Optional<List> oldSpawn = Settings.getRaw("spawn", List.class);
        List<List> spawns = Settings.getList("spawn_new").orElse(new ArrayList<>());
        if(oldSpawn.isPresent()) {
            spawns.add(oldSpawn.get());
            this.spawn = lc.convertLoad(oldSpawn.get());
        }
        else {
            if(spawns.isEmpty()) {
                this.spawn = SpleefLeague.DEFAULT_WORLD.getSpawnLocation();
                spawns.add(lc.convertSave(this.spawn));
                System.err.println("No spawn defined, using default world spawn!");
            }
            else {
                this.spawn = lc.convertLoad(spawns.get(0));
            }   
        }
        List<SpawnLocation> spawnLocations = spawns
                .stream()
                .map(lc::convertLoad)
                .map(SpawnManager.SpawnLocation::new)
                .collect(Collectors.toList());
        spawnManager = new SpawnManager(spawnLocations, Settings.getInteger("spawn_max_players").orElse(50));
        
        Settings.getInteger("max_players").ifPresent(s -> setSlotSize(s));
        
        Settings.getDocument("game_rules").ifPresent(d -> d.forEach((String key, Object object) -> CorePlugin.DEFAULT_WORLD.setGameRuleValue(key, object.toString())));
        
        if(Config.hasKey("server_type")) {
            serverType = ServerType.valueOf(Config.getString("server_type").toUpperCase());
        }
        else {
            serverType = ServerType.MAIN;
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

    /**
     * Initializes MongoDB from sl.conf file, if a port is
     * not specified then it's assumed that the port is
     * already in the host string
     */
    private void initMongo() {
        try {
            List<MongoCredential> credentials = Config.getCredentials();
            String host = Config.getString("host");
            String port = Config.getString("port");
            ServerAddress address;
            if (port != null) {
                address = new ServerAddress(host + ":" + port);
            } else {
                address = new ServerAddress(host);
            }
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
            Object playerlist = Class
                    .forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer")
                    .getDeclaredMethod("getHandle")
                    .invoke(Bukkit.getServer());
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

    public DBPlayerManager<SLPlayer> getPlayerManager() {
        return playerManager;
    }

    public ConnectionClient getConnectionClient() {
        return connectionClient;
    }

    public AutoBroadcaster getAutoBroadcaster() {
        return autoBroadcaster;
    }

    public DebuggerHostManager getDebuggerHostManager() {
        return this.debuggerHostManager;
    }
    
    public ServerType getServerType() {
        return serverType;
    }

    public ProtocolManager getProtocolManager() {
        return ProtocolLibrary.getProtocolManager();
    }

    // Use the SpawnManager instead.
    @Deprecated
    public Location getSpawnLocation() {
        return spawn;
    }

    @Override
    public void syncSave(Player p) {
        SLPlayer slp = playerManager.get(p);
        if (slp != null) {
            EntityBuilder.save(slp, getPluginDB().getCollection("Players"));
        }
    }
    
    private String getImplVersion() {
        String version = SpleefLeague.class.getPackage().getImplementationVersion();
        if(version == null)
            return "unknown";
        return version;
    }
    
    public String getCommitId() {
        String version = getImplVersion();
        if(version.equals("unknown"))
            return version;
        return version.split("\\-")[0];
    }
    
    public String getCommitDate() {
        String version = getImplVersion();
        if(version.equals("unknown"))
            return "";
        return version.split("\\-")[1];
    }

    private static SpleefLeague instance;

    public static SpleefLeague getInstance() {
        return instance;
    }
}
