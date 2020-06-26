package com.spleefleague.core.utils.recording;

import com.google.gson.internal.Primitives;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProtocolProvider {
    private static ProtocolProvider protocolProvider = new ProtocolProvider();

    public static ProtocolProvider getProtocolProvider() {
        return protocolProvider;
    }

    private Field b = getDeclaredField("b");
    private Field a = getDeclaredField("a");
    private Map<Class<?>, Object> dataWatcherSerializers;
    {
        dataWatcherSerializers = new HashMap<>();
        Field[] fields = DataWatcherRegistry.class.getFields();
        for (Field field : fields) {
            try {
                dataWatcherSerializers.put(getGenericType(field), field.get(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Field getDeclaredField(String fieldName)  {
        try {
            Field field = PacketPlayOutEntityMetadata.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return the generic type of a field, in this case of a {@link DataWatcherSerializer} with the generic of T, T being the value we want to retrieve.
     * @param field The actual field we want to retrieve the generic class from.
     * @return Class of the generic associated with this field.
     */
    private Class<?> getGenericType(Field field){
        ParameterizedType parameterizedType =  (ParameterizedType) field.getGenericType();
        Type type = parameterizedType.getActualTypeArguments()[0];
        if (type instanceof Class<?>){
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * Get serializer of supplied type from the {@link this#dataWatcherSerializers} map.
     * @param val Value that is in need of a serializer.
     * @param <T> The type of the value.
     * @return Serializer of value T
     */
    private <T> DataWatcherSerializer<T> getSerializer(T val){
        Object serializer = dataWatcherSerializers.get(val.getClass());
        if (serializer == null)
            serializer = dataWatcherSerializers.get(Primitives.wrap(val.getClass()));
        return serializer == null ? null : ((DataWatcherSerializer<T>) serializer);
    }

    /**
     * Returns a {@link DataWatcherObject} of type corresponding to val, with the included data of id.
     * @param val The value that needs an object for itself.
     * @param id The id to be held inside the DataWatcherObject
     * @param <T> The Type of val.
     * @return
     */
    private <T> DataWatcherObject<T> getObject(T val, byte id){
        DataWatcherSerializer<T> dataWatcherSerializer = getSerializer(val);
        return dataWatcherSerializer == null ? null : dataWatcherSerializer.a(id);
    }

    /**
     * Create an empty packet of {@link PacketPlayOutEntityMetadata} with the ID of entity included.
     * @param entity The entity that should be affected by this packet.
     * @return An empty PacketPlayOutEntityMetadata that should affect the entity above.
     * @throws IllegalAccessException Reflection...
     */
    private PacketPlayOutEntityMetadata createEmptyPacket(int entityId) throws IllegalAccessException {
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata();
        a.set(packetPlayOutEntityMetadata, entityId);
        return packetPlayOutEntityMetadata;
    }

    /**
     * Send a metadata packet to player with the supplied arguments, that should affect the LivingEntity. Cheers
     * This took way too long to figure out how to do... Seems easy now though.
     * @param entityId LivingEntity that should be affected by the Packet.
     * @param id The Byte id of the packet.
     * @param value The actual value/data of the packet.
     * @param <T> The Type of value, used to infer what serializer should be used.
     * @throws IllegalAccessException
     */
    public <T> PacketPlayOutEntityMetadata getMetadataPacket(int entityId, byte id, T value) throws IllegalAccessException {
        PacketPlayOutEntityMetadata packetPlayOutEntityMetadata = createEmptyPacket(entityId);
        b.set(packetPlayOutEntityMetadata, Collections.singletonList(new DataWatcher.Item<>(getObject(value, id), value)));
        return packetPlayOutEntityMetadata;
    }

    /**
     * Send a packet to a player.
     * @param player The Player who will receive the packet.
     * @param packet Packet the player will receive.
     */
    public void sendPacket(Player player, Packet packet){
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}