package com.spleefleague.core.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.virtualworld.VirtualWorld;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author balsfull
 */
public class ArtificialThrottleAdapter extends PacketAdapter implements Listener {
    
    private final DelayQueue<DelayedPacket> packetQueueIn = new DelayQueue<>();
    private final DelayQueue<DelayedPacket> packetQueueOut = new DelayQueue<>();
    private final Set<Integer> delayed = ConcurrentHashMap.newKeySet();
    private final Map<Player, AtomicInteger> delayCountIn = new ConcurrentHashMap<>();
    private final Map<Player, AtomicInteger> delayCountOut = new ConcurrentHashMap<>();
    
    public ArtificialThrottleAdapter() {
        super(VirtualWorld.getInstance(), ListenerPriority.LOWEST, new PacketType[]{ 
            Client.USE_ENTITY, 
            Client.FLYING, 
            Client.POSITION, 
            Client.POSITION_LOOK, 
            Client.LOOK, 
            Client.VEHICLE_MOVE, 
            Client.BOAT_MOVE, 
            Client.ABILITIES, 
            Client.BLOCK_DIG, 
            Client.ENTITY_ACTION, 
            Client.STEER_VEHICLE, 
            Client.ARM_ANIMATION, 
            Client.USE_ITEM, 
            Client.BLOCK_PLACE,
            Server.SPAWN_ENTITY,
            Server.SPAWN_ENTITY_EXPERIENCE_ORB,
            Server.SPAWN_ENTITY_WEATHER,
            Server.SPAWN_ENTITY_LIVING,
            Server.SPAWN_ENTITY_PAINTING,
            Server.NAMED_ENTITY_SPAWN,
            Server.ANIMATION,
            Server.BLOCK_BREAK_ANIMATION,
            Server.TILE_ENTITY_DATA,
            Server.BLOCK_ACTION,
            Server.BLOCK_CHANGE,
            Server.MULTI_BLOCK_CHANGE,
            Server.SET_SLOT,
            Server.SET_COOLDOWN,
            Server.CUSTOM_PAYLOAD,
            Server.CUSTOM_SOUND_EFFECT,
            Server.ENTITY_STATUS,
            Server.EXPLOSION,
            Server.UNLOAD_CHUNK,
            Server.GAME_STATE_CHANGE,
            Server.MAP_CHUNK,
            Server.WORLD_EVENT,
            Server.WORLD_PARTICLES,
            Server.MAP,
            Server.ENTITY,
            Server.REL_ENTITY_MOVE,
            Server.REL_ENTITY_MOVE_LOOK,
            Server.ENTITY_LOOK,
            Server.VEHICLE_MOVE,
            Server.ABILITIES,
            Server.COMBAT_EVENT,
            Server.PLAYER_INFO,
            Server.POSITION,
            Server.ENTITY_DESTROY,
            Server.REMOVE_ENTITY_EFFECT,
            Server.RESPAWN,
            Server.ENTITY_HEAD_ROTATION,
            Server.WORLD_BORDER,
            Server.CAMERA,
            Server.HELD_ITEM_SLOT,
            Server.SCOREBOARD_DISPLAY_OBJECTIVE,
            Server.ENTITY_METADATA,
            Server.ATTACH_ENTITY,
            Server.ENTITY_VELOCITY,
            Server.ENTITY_EQUIPMENT,
            Server.EXPERIENCE,
            Server.UPDATE_HEALTH,
            Server.SCOREBOARD_OBJECTIVE,
            Server.MOUNT,
            Server.SCOREBOARD_TEAM,
            Server.SCOREBOARD_SCORE,
            Server.SPAWN_POSITION,
            Server.UPDATE_TIME,
            Server.TITLE,
            Server.NAMED_SOUND_EFFECT,
            Server.PLAYER_LIST_HEADER_FOOTER,
            Server.COLLECT,
            Server.ENTITY_TELEPORT,
            Server.UPDATE_ATTRIBUTES,
            Server.ENTITY_EFFECT
        });
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                while(true) {
                        DelayedPacket dp = packetQueueIn.take();
                        manager.recieveClientPacket(dp.getSender(), dp.getPacket());
                        delayCountIn.get(dp.getSender()).decrementAndGet();
                }
            } catch (InterruptedException | IllegalAccessException | InvocationTargetException ex) {}
        });
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                while(true) {
                        DelayedPacket dp = packetQueueOut.take();
                        manager.sendServerPacket(dp.getSender(), dp.getPacket());
                        int c = delayCountOut.get(dp.getSender()).decrementAndGet();
                }
            } catch (InterruptedException | InvocationTargetException ex) {}
        });
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        delayCountIn.put(event.getPlayer(), new AtomicInteger(0));
        delayCountOut.put(event.getPlayer(), new AtomicInteger(0));
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        delayCountIn.remove(event.getPlayer());
        delayCountOut.remove(event.getPlayer());
    }
    
    public void decreaseDelay(Player player, Duration difference) {
        difference = difference.dividedBy(2);
        Set<DelayedPacket> changed = new LinkedHashSet<>();
        for(DelayedPacket packet : packetQueueIn) {
            if(packet.getSender() == player) {
                packet.setDelay(packet.getDelay().minus(difference));
                changed.add(packet);
            }
        }
        packetQueueIn.removeAll(changed);
        packetQueueIn.addAll(changed);
        changed.clear();
        for(DelayedPacket packet : packetQueueOut) {
            if(packet.getSender() == player) {
                packet.setDelay(packet.getDelay().minus(difference));
                changed.add(packet);
            }
        }
        packetQueueOut.removeAll(changed);
        packetQueueOut.addAll(changed);
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
        if(slp == null) return;
        int ping = slp.getPing();
        if(ping >= slp.getAimedPing() && delayCountOut.get(event.getPlayer()).get() == 0) return;
        int hash = event.getPacket().getHandle().hashCode();
        if(!delayed.contains(hash)) {
            delayed.add(hash);
            delayCountOut.get(slp.getPlayer()).incrementAndGet();
            long delay = (slp.getAimedPing() - ping) / 2;
            packetQueueOut.add(new DelayedPacket(Instant.now(), Duration.of(delay, ChronoUnit.MILLIS), event.getPacket(), event.getPlayer()));
            event.setCancelled(true);
        }
        else {
            delayed.remove(hash);
        }
    }
    
    @Override
    public void onPacketReceiving(PacketEvent event) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
        if(slp == null) return;
        int ping = slp.getPing();
        if(ping >= slp.getAimedPing() && delayCountIn.get(event.getPlayer()).get() == 0) return;
        int hash = event.getPacket().getHandle().hashCode();
        if(!delayed.contains(hash)) {
            delayed.add(hash);
            delayCountIn.get(slp.getPlayer()).incrementAndGet();
            long delay = (slp.getAimedPing() - ping) / 2;
            packetQueueIn.add(new DelayedPacket(Instant.now(), Duration.of(delay, ChronoUnit.MILLIS), event.getPacket(), event.getPlayer()));
            event.setCancelled(true);
        }
        else {
            delayed.remove(hash);
        }
    }
    
    private static class DelayedPacket implements Delayed {

        private final Instant creation;
        private final PacketContainer packet;
        private final Player sender;
        private Duration delay;

        public DelayedPacket(Instant creation, Duration delay, PacketContainer packet, Player sender) {
            this.creation = creation;
            this.delay = delay;
            this.packet = packet;
            this.sender = sender;
        }

        public void setDelay(Duration delay) {
            this.delay = delay;
        }

        public PacketContainer getPacket() {
            return packet;
        }

        public Player getSender() {
            return sender;
        }
        
        @Override
        public long getDelay(TimeUnit unit) {
            Temporal base = Instant.now().minus(delay);
            return base.until(creation, convert(unit));
        }
        
        public Duration getDelay() {
            return delay;
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }
        
        public static ChronoUnit convert(TimeUnit tu) {
            if (tu == null) {
                return null;
            }
            switch (tu) {
                case DAYS:
                    return ChronoUnit.DAYS;
                case HOURS:
                    return ChronoUnit.HOURS;
                case MINUTES:
                    return ChronoUnit.MINUTES;
                case SECONDS:
                    return ChronoUnit.SECONDS;
                case MICROSECONDS:
                    return ChronoUnit.MICROS;
                case MILLISECONDS:
                    return ChronoUnit.MILLIS;
                case NANOSECONDS:
                    return ChronoUnit.NANOS;
                default:
                    assert false : "there are no other TimeUnit ordinal values";
                    return null;
            }
        }
    }
}