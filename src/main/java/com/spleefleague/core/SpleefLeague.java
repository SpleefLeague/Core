package com.spleefleague.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.chat.discord.JDAHandler;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.command.CommandLoader;
import com.spleefleague.core.cosmetics.CosmeticsManager;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.Settings;
import com.spleefleague.core.io.TypeConverter;
import com.spleefleague.core.io.TypeConverter.LocationConverter;
import com.spleefleague.core.io.connections.ConnectionClient;
import com.spleefleague.core.listeners.*;
import com.spleefleague.core.menus.InventoryMenuTemplateRepository;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.portals.PortalManager;
import com.spleefleague.core.queue.Challenge;
import com.spleefleague.core.spawn.SpawnManager;
import com.spleefleague.core.spawn.SpawnManager.SpawnLocation;
import com.spleefleague.core.utils.*;
import com.spleefleague.core.utils.debugger.DebuggerHostManager;
import com.spleefleague.core.utils.fakeentity.FakeEntitiesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Jonas
 */
public class SpleefLeague extends CorePlugin {

    public static final String BROADCAST_FORMAT = ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Broadcast"
            + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "%s";
    
    private static ProtocolManager manager;
    
    public static ProtocolManager getProtocolManager() {
        return manager;
    }

    private MongoClient mongo;
    private Rank minimumJoinRank;
    private List<Rank> extraJoinRanks;
    private PlayerManager<SLPlayer> playerManager;
    private AutoBroadcaster autoBroadcaster;
    private Location spawn;
    private ConnectionClient connectionClient;
    private CommandLoader commandLoader;
    private SpawnManager spawnManager;
    private PortalManager portalManager;
    private DynamicCommandManager dynamicCommandManager;
    private DebuggerHostManager debuggerHostManager;
    private ServerType serverType;
    private JDAHandler discordApi;
    
    public SpleefLeague() {
        super("[SpleefLeague]", ChatColor.GRAY + "[" + ChatColor.GOLD + "SpleefLeague" + ChatColor.GRAY + "]" + ChatColor.RESET);
    }

    @Override
    public void start() {
        //The order is important
        instance = this;
        manager = ProtocolLibrary.getProtocolManager();
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
        new FakeEntitiesManager();
        autoBroadcaster = new AutoBroadcaster(getMongo().getDatabase("SpleefLeague").getCollection("AutoBroadcaster"));
        playerManager = new PlayerManager<>(this, SLPlayer.class);
        portalManager = new PortalManager();
        connectionClient = new ConnectionClient();
        dynamicCommandManager = new DynamicCommandManager(this);
        debuggerHostManager = new DebuggerHostManager();
        Settings.getList("debugger_hosts").ifPresent(hosts -> debuggerHostManager.reloadAll(hosts));
        Settings.getString("discord_token").ifPresent(t -> discordApi = new JDAHandler(t));
        loadServerType();
    }

    @Override
    public void stop() {
        playerManager.saveAll();
        autoBroadcaster.stopTask();
        connectionClient.stop();
        mongo.close();
    }

    public BasicCommand getBasicCommand(String name) {
        return commandLoader.getCommand(name).getExecutor();
    }

    public void applySettings() {
        if (Settings.hasKey("default_world")) {
            String defaultWorld = Settings.getString("default_world").get();
            CorePlugin.DEFAULT_WORLD = Bukkit.getWorld(defaultWorld);
        }
        
        if (Settings.hasKey("spawn_new") && Settings.hasKey("spawn_max_players")) {
            LocationConverter lc = new TypeConverter.LocationConverter();
            List<SpawnLocation> spawns = ((List<List>) Settings.getList("spawn_new").get())
                    .stream()
                    .map(lc::convertLoad)
                    .map(SpawnManager.SpawnLocation::new)
                    .collect(Collectors.toList());
            spawnManager = new SpawnManager(spawns, Settings.getInteger("spawn_max_players").getAsInt());
        } 
        Settings.getLocation("spawn").ifPresent(s -> CorePlugin.DEFAULT_WORLD.setSpawnLocation(s.getBlockX(), s.getBlockY(), s.getBlockZ()));
        Settings.getInteger("max_players").ifPresent(s -> setSlotSize(s));
        Settings.getDocument("game_rules").ifPresent(d -> d.forEach((String key, Object object) -> CorePlugin.DEFAULT_WORLD.setGameRuleValue(key, object.toString())));
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
    
    private void loadServerType() {
        serverType = ServerType.MAIN;
        FileConfiguration config = getConfig();
        if(!config.isSet("server_type")) {
            config.set("server_type", "MAIN");
            saveConfig();
            return;
        }
        try {
            serverType = ServerType.valueOf(config.getString("server_type").toUpperCase());
        }catch(Exception ex) {}
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

    public AutoBroadcaster getAutoBroadcaster() {
        return autoBroadcaster;
    }

    public DebuggerHostManager getDebuggerHostManager() {
        return this.debuggerHostManager;
    }
    
    public ServerType getServerType() {
        return serverType;
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
