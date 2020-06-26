package com.spleefleague.core.utils.recording;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.spleefleague.entitybuilder.*;
import net.minecraft.server.v1_15_R1.EntityPose;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ActiveRecordingData implements RecordingData {

    private int tick = 0;
    private List<StateChange> stateChanges;
    private Location start;

    public ActiveRecordingData(Location start) {
        stateChanges = new ArrayList<>();
        this.start = start;
    }

    public Location getStart() {
        return start;
    }

    public List<StateChange> getStateChanges() {
        return stateChanges;
    }

    public void tick() {
        tick++;
    }

    public void appendLocation(double x, double y, double z) {
        stateChanges.add(new Position(tick, x, y, z));
    }

    public void appendLocation(double x, double y, double z, double pitch, double yaw) {
        stateChanges.add(new PositionLook(tick, x, y, z, pitch, yaw));
    }

    public void setSneaking(boolean sneaking) {
        stateChanges.add(new SneakState(tick, sneaking));
    }

    public void setSprinting(boolean sprinting) {
        stateChanges.add(new SprintState(tick, sprinting));
    }

    public static class StateChangeConverter extends TypeConverter<Document, StateChange> {

        @Override
        public StateChange convertLoad(Document document) {
            StateChangeType type = StateChangeType.valueOf(document.getString("type"));
            switch(type) {
                case REL_POSITION: return EntityBuilder.deserialize(document, Position.class);
                case REL_POSITION_LOOK: return EntityBuilder.deserialize(document, PositionLook.class);
                case SNEAKING: return EntityBuilder.deserialize(document, SneakState.class);
                case SPRINTING: return EntityBuilder.deserialize(document, SprintState.class);
                default: throw new UnsupportedOperationException("Unknown type: " + type);
            }
        }

        @Override
        public Document convertSave(StateChange stateChange) {
            return EntityBuilder.serialize(stateChange).get("$set", Document.class);
        }
    }

    public static abstract class StateChangeData extends DBEntity implements DBLoadable, DBSaveable, StateChange {

        @DBLoad(fieldName = "tick")
        @DBSave(fieldName = "tick")
        private int tick;

        @DBSave(fieldName = "type")
        public abstract StateChangeType getType();

        public StateChangeData(int tick){
            this.tick = tick;
        }

        @Override
        public int getTick() {
            return tick;
        }
    }

    public enum StateChangeType {
        TELEPORT,
        REL_POSITION,
        REL_POSITION_LOOK,
        SNEAKING,
        SPRINTING
    }

    public static class Position extends StateChangeData {
        @DBLoad(fieldName = "x")
        @DBSave(fieldName = "x")
        public double x;
        @DBLoad(fieldName = "y")
        @DBSave(fieldName = "y")
        public double y;
        @DBLoad(fieldName = "z")
        @DBSave(fieldName = "z")
        public double z;

        public Position(int tick, double x, double y, double z) {
            super(tick);
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public StateChangeType getType() {
            return StateChangeType.REL_POSITION;
        }

        @Override
        public void applyTo(int entityId, boolean forward, boolean ghost, Player... audience) {
            PacketContainer container = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
            double coordConstant = forward ? 4096D : -4096D;
            container.getShorts().write(0, (short)(x * coordConstant));
            container.getShorts().write(1, (short)(y * coordConstant));
            container.getShorts().write(2, (short)(z * coordConstant));
            container.getIntegers().write(0, entityId);
            for(Player player : audience) {
                if(!player.isOnline()) continue;
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class PositionLook extends Position {
        @DBLoad(fieldName = "pitch")
        @DBSave(fieldName = "pitch")
        public double pitch;
        @DBLoad(fieldName = "yaw")
        @DBSave(fieldName = "yaw")
        public double yaw;

        public PositionLook(int tick, double x, double y, double z, double pitch, double yaw) {
            super(tick, x, y, z);
            this.pitch = pitch;
            this.yaw = yaw;
        }

        @Override
        public StateChangeType getType() {
            return StateChangeType.REL_POSITION_LOOK;
        }

        @Override
        public void applyTo(int entityId, boolean forward, boolean ghost, Player... audience) {
            PacketContainer moveLook = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
            double coordConstant = forward ? 4096D : -4096D;
            moveLook.getShorts().write(0, (short)(x * coordConstant));
            moveLook.getShorts().write(1, (short)(y * coordConstant));
            moveLook.getShorts().write(2, (short)(z * coordConstant));
            moveLook.getBytes().write(0, (byte)(yaw * 256.0f / 360.0f));
            moveLook.getBytes().write(1, (byte)(pitch * 256.0f / 360.0f));
            moveLook.getBooleans().write(0, true);
            moveLook.getIntegers().write(0, entityId);
            PacketContainer look = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            look.getBytes().write(0, (byte)(yaw * 256.0f / 360.0f));
            look.getIntegers().write(0, entityId);
            for(Player player : audience) {
                if(!player.isOnline()) continue;
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, moveLook);
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, look);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class SprintState extends StateChangeData {
        @DBLoad(fieldName = "sprinting")
        @DBSave(fieldName = "sprinting")
        public final boolean sprinting;

        public SprintState(int tick, boolean sprinting) {
            super(tick);
            this.sprinting = sprinting;
        }

        @Override
        public StateChangeType getType() {
            return StateChangeType.SPRINTING;
        }

        @Override
        public void applyTo(int entityId, boolean forward, boolean ghost, Player... audience) {
            boolean sprinting = forward ? this.sprinting : !this.sprinting;
            int additional = ghost ? 0x60 : 0x00;
            for(Player p : audience) {
                try {
                    if(!p.isOnline()) continue;
                    PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
                    packet.getIntegers().write(0, entityId);
                    WrappedDataWatcher watcher = new WrappedDataWatcher();
                    WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
                    watcher.setEntity(p);
                    watcher.setObject(0, serializer, (byte)(additional + (sprinting ? 0x08 : 0x00)));
                    packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class SneakState extends StateChangeData {
        @DBLoad(fieldName = "sneaking")
        @DBSave(fieldName = "sneaking")
        public final boolean sneaking;

        public SneakState(int tick, boolean sneaking) {
            super(tick);
            this.sneaking = sneaking;
        }

        @Override
        public StateChangeType getType() {
            return StateChangeType.SNEAKING;
        }

        @Override
        public void applyTo(int entityId, boolean forward, boolean ghost, Player... audience) {
            boolean sneaking = forward ? this.sneaking : !this.sneaking;
            int additional = ghost ? 0x60 : 0x00;
            for(Player p : audience) {
                try {
                    if(!p.isOnline()) continue;
                    PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
                    packet.getIntegers().write(0, entityId); //Set packet's entity id
                    WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
                    watcher.setEntity(p); //Set the new data watcher's target
                    watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), (byte)(additional + (sneaking ? 0x02 : 0x00)));
                    watcher.setObject(6, WrappedDataWatcher.Registry.get(EntityPose.class), sneaking ? EntityPose.CROUCHING : EntityPose.STANDING);
                    packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
