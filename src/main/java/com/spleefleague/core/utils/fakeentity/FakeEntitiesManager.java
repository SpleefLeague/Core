package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.events.FakeCreatureInteractEvent;
import com.spleefleague.core.utils.Task;
import com.spleefleague.core.utils.UtilChat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class FakeEntitiesManager implements Listener {
    
    private final static Object lock = new Object();
    
    private volatile static boolean enabled = true;
    private volatile static boolean useFakeNpcInteractEvent = true;
    
    public static boolean isUsingFakeNpcInteractEvent() {
        return useFakeNpcInteractEvent;
    }
    
    public static void setUsingFakeNpcInteractEvent(boolean value) {
        useFakeNpcInteractEvent = value;
    }
    
    private final static Map<Integer, FakeCreature> creatures = new HashMap<>();
    
    public static Map<Integer, FakeCreature> getCreatures() {
        return creatures;
    }
    
    private final static LoadingCache<String, Boolean> rightClickUsageCache = CacheBuilder.newBuilder()
            .initialCapacity(500).expireAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Boolean>() {
                
        @Override
        public Boolean load(String k) {
            return false;
        }
        
    });
    
    public FakeEntitiesManager() {
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
        Task.schedule(() -> FakeCreature.getAlive().forEach(FakeCreature::tick), 0l, 40l);
        SpleefLeague.getProtocolManager().addPacketListener(new PacketAdapter(
            SpleefLeague.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {

                @Override
                public void onPacketReceiving(PacketEvent event) {
                    if(!enabled)
                        return;
                    WrapperPlayClientUseEntity wrapper = new WrapperPlayClientUseEntity(event.getPacket());
                    synchronized(lock) {
                        if(creatures.containsKey(wrapper.getTargetID())) {
                            Task.schedule(() -> {
                                synchronized(lock) {
                                    FakeCreature npc = creatures.get(wrapper.getTargetID());
                                    Player p = event.getPlayer();
                                    if(FakeCreaturesWorker.isPending(p)) {
                                        FakeCreaturesWorker.addWorking(p, npc.getId());
                                        UtilChat.s(Theme.SUCCESS, p, "Now you're working with that creature.");
                                        return;
                                    }
                                    EntityUseAction action = wrapper.getType();
                                    ClickType ct = action == EntityUseAction.ATTACK ? ClickType.LEFT :
                                            action == EntityUseAction.INTERACT ? ClickType.RIGHT : null;
                                    if(ct == null)
                                        return;
                                    FakeCreatureInteractEvent fcie = useFakeNpcInteractEvent ? new FakeCreatureInteractEvent(p, npc, ct) : null;
                                    if(ct == ClickType.LEFT) {
                                        if(useFakeNpcInteractEvent) {
                                            Bukkit.getPluginManager().callEvent(fcie);
                                            if(fcie.isCancelled())
                                                return;
                                        }
                                        npc.onLeftClick(p);
                                    }else if(ct == ClickType.RIGHT) {
                                        if(rightClickUsageCache.getIfPresent(p.getName()) != null)
                                            return;
                                        if(useFakeNpcInteractEvent) {
                                            Bukkit.getPluginManager().callEvent(fcie);
                                            if(fcie.isCancelled())
                                                return;
                                        }
                                        rightClickUsageCache.getUnchecked(p.getName());
                                        npc.onRightClick(p);
                                    }
                                }
                            });
                        }
                    }
                }

            }
        );
        new FakeCreaturesWorker();
        FileConfiguration config = getConfig();
        List<String> npcs = config.isSet("npcs") ? config.getStringList("npcs") : new ArrayList<>();
        for(String npc : npcs)
            try {
                ((FakeNpc) FakeNpc.deserialize(npc)).spawn();
            }catch(Exception ex) {
                SpleefLeague.LOG.warning("Can not deserialize fake npc!\nNpc data:\n" + npc);
                ex.printStackTrace();
            }
    }
    
    public static void unloadAll() {
        synchronized(lock) {
            creatures.values().forEach(FakeCreature::despawn);
        }
    }
    
    static void addCreature(int id, FakeCreature creature) {
        synchronized(lock) {
            creatures.put(id, creature);
        }
        enabled = true;
    }
    
    static void removeCreature(FakeCreature creature) {
        synchronized(lock) {
            creatures.remove(creature.getId());
        }
    }
    
    private static FileConfiguration getConfig() {
        return SpleefLeague.getInstance().getConfig();
    }
    
    private static void saveConfig() {
        SpleefLeague.getInstance().saveConfig();
    }
    
    public static FakeCreature getCreature(int id) {
        synchronized(lock) {
            return creatures.get(id);
        }
    }
    
    static void save(FakeNpc npc) {
        delete(npc);
        FileConfiguration config = getConfig();
        List<String> npcs = config.isSet("npcs") ? config.getStringList("npcs") : new ArrayList<>();
        npcs.add(npc.serialize(null));
        config.set("npcs", npcs);
        saveConfig();
    }
    
    static void delete(FakeNpc npc) {
        FileConfiguration config = getConfig();
        List<String> npcs = config.isSet("npcs") ? config.getStringList("npcs") : new ArrayList<>();
        String uuid = npc.getUuid().toString();
        if(!npcs.removeIf(s -> s.contains(uuid)))
            return;
        config.set("npcs", npcs);
        saveConfig();
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        FakeNpcScoreboard.register(p);
    }
    
}
