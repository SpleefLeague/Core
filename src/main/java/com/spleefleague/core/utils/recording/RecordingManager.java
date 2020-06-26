package com.spleefleague.core.utils.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.entitybuilder.EntityBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RecordingManager implements Listener {
    private static final FieldAccessor ENTITY_ID = Accessors.getFieldAccessor(
            MinecraftReflection.getEntityClass(), "entityCount", true);
    private Map<UUID, ActiveRecordingData> recordedPlayers = new HashMap<>();

    public RecordingManager() {
        Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> recordedPlayers.values().forEach(ActiveRecordingData::tick), 0, 1);
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
    }

    public void startRecording(Player player) {
        if(!player.isOnline()) throw new IllegalArgumentException("Player must be online");
        ActiveRecordingData recordingData = new ActiveRecordingData(player.getLocation());
        recordedPlayers.put(player.getUniqueId(), recordingData);
    }

    public Recording stopRecording(UUID playerId) {
        ActiveRecordingData recordingData = recordedPlayers.get(playerId);
        recordedPlayers.remove(playerId);
        Recording recording = new Recording(playerId, recordingData.getStart());
        recording.setRecordingData(recordingData);
        return recording;
    }

    public ObjectId saveRecording(Recording recording) {
        EntityBuilder.save(recording, SpleefLeague.getInstance().getPluginDB().getCollection("Recordings"));
        return recording.getObjectId();
    }

    public Recording loadRecording(ObjectId id) {
        return EntityBuilder.load(SpleefLeague.getInstance().getPluginDB().getCollection("Recordings").find(new Document("_id", id)).first(), Recording.class);
    }

    public Replay playRecording(Recording recording, Player target) {
        return playRecording(recording, target, false);
    }

    public Replay playRecording(Recording recording, Player target, boolean ghost) {
        PacketContainer entitySpawnContainer = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        int entityId = ((AtomicInteger)ENTITY_ID.get(null)).incrementAndGet();
        entitySpawnContainer.getIntegers().write(0, entityId);
        entitySpawnContainer.getUUIDs().write(0, target.getUniqueId());
        entitySpawnContainer.getDoubles().write(0, recording.getStart().getX());
        entitySpawnContainer.getDoubles().write(1, recording.getStart().getY());
        entitySpawnContainer.getDoubles().write(2, recording.getStart().getZ());
        entitySpawnContainer.getBytes().write(0, (byte)(recording.getStart().getYaw() * 256.0F / 360.0F));
        entitySpawnContainer.getBytes().write(1, (byte)(recording.getStart().getPitch() * 256.0F / 360.0F));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(target, entitySpawnContainer);
            if(ghost) {
                PacketContainer ghostContainer = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
                ghostContainer.getIntegers().write(0, entityId);
                WrappedDataWatcher watcher = new WrappedDataWatcher();
                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                watcher.setEntity(target);
                watcher.setObject(0, serializer, (byte)0x60);
                ghostContainer.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
                ProtocolLibrary.getProtocolManager().sendServerPacket(target, ghostContainer);
            }
            Replay replay = new Replay(recording, target, entityId, ghost);
            BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(replay.isDone()) {
                        replay.cleanup();
                        this.cancel();
                    }
                    else {
                        replay.doTick();
                    }
                }
            };
            bukkitRunnable.runTaskTimer(SpleefLeague.getInstance(), 0, 1);
            return replay;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        recordedPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        ActiveRecordingData recordingData = recordedPlayers.get(event.getPlayer().getUniqueId());
        if(recordingData == null) return;
        Vector delta = event.getTo().toVector().subtract(event.getFrom().toVector());
        recordingData.appendLocation(delta.getX(), delta.getY(), delta.getZ(), event.getPlayer().getLocation().getPitch(), event.getPlayer().getLocation().getYaw());
    }

    @EventHandler
    public void onSneaking(PlayerToggleSneakEvent event) {
        ActiveRecordingData recordingData = recordedPlayers.get(event.getPlayer().getUniqueId());
        if(recordingData == null) return;
        recordingData.setSneaking(event.isSneaking());
    }

    @EventHandler
    public void onSprinting(PlayerToggleSprintEvent event) {
        ActiveRecordingData recordingData = recordedPlayers.get(event.getPlayer().getUniqueId());
        if(recordingData == null) return;
        recordingData.setSprinting(event.isSprinting());
    }
}
