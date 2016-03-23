/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.chat;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBLoadable;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.TypeConverter;
import java.util.HashMap;
import java.util.Map;
import com.spleefleague.core.player.Rank;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 * @author Jonas
 */
public class ChatChannel extends DBEntity implements DBLoadable, Comparable<ChatChannel> {

    @DBLoad(fieldName = "minRank", typeConverter = Rank.FromStringConverter.class)
    private Rank minRank;
    @DBLoad(fieldName = "name")
    private String name;
    @DBLoad(fieldName = "displayName")
    private String displayName;
    @DBLoad(fieldName = "defaultChannel")
    private boolean defaultChannel;
    @DBLoad(fieldName = "visible")
    private boolean visible;
    private boolean temporary = false;
    @DBLoad(fieldName = "order")
    private int order;

    private ChatChannel() {

    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Rank getMinRank() {
        return minRank;
    }

    public void setMinRank(Rank minRank) {
        this.minRank = minRank;
    }

    public boolean isDefault() {
        return defaultChannel;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public int compareTo(ChatChannel o) {
        return Integer.compare(o.order, order);
    }

    private static Map<String, ChatChannel> channels = new HashMap<>();

    public static ChatChannel createTemporaryChannel(String name, String displayName, Rank minRank, boolean defaultChannel, boolean visible) {
        ChatChannel channel = new ChatChannel();
        channel.name = name;
        channel.displayName = displayName;
        channel.minRank = minRank;
        channel.defaultChannel = defaultChannel;
        channel.visible = visible;
        channel.temporary = true;
        channel.order = Integer.MAX_VALUE;
        channels.put(name, channel);
        ChatManager.registerChannel(channel);
        return channel;
    }

    public static ChatChannel fromString(String channel) {
        return channels.get(channel);
    }

    public static final ChatChannel GLOBAL = getPlaceholderInstance(),
            STAFF = getPlaceholderInstance(),
            STAFF_NOTIFICATIONS = getPlaceholderInstance();

    public static ChatChannel valueOf(String name) {
        return channels.get(name);
    }

    public static ChatChannel[] values() {
        return channels.values().toArray(new ChatChannel[0]);
    }

    private static ChatChannel getPlaceholderInstance() {
        ChatChannel channel = new ChatChannel();
        channel.name = "PLACEHOLDER";
        channel.displayName = "PLACEHOLDER";
        channel.defaultChannel = false;
        channel.visible = false;
        channel.minRank = Rank.ADMIN;
        return channel;
    }

    public static void init() {
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getPluginDB().getCollection("ChatChannels").find().iterator();
        while (dbc.hasNext()) {
            ChatChannel channel = EntityBuilder.load(dbc.next(), ChatChannel.class);
            ChatChannel staticChannel = getField(channel.getName());
            if (staticChannel != null) {
                staticChannel.name = channel.name;
                staticChannel.displayName = channel.displayName;
                staticChannel.defaultChannel = channel.defaultChannel;
                staticChannel.visible = channel.visible;
                staticChannel.minRank = channel.minRank;
                channel = staticChannel;
            }
            channels.put(channel.getName(), channel);
            ChatManager.registerChannel(channel, true);
        }
        SpleefLeague.getInstance().log("Loaded " + channels.size() + " chat channels!");
    }

    private static ChatChannel getField(String name) {
        try {
            Field field = ChatChannel.class.getField(name);
            ChatChannel staticChannel = (ChatChannel) field.get(null);
            return staticChannel;
        } catch (NoSuchFieldException e) {
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Rank.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static class FromStringConverter extends TypeConverter<String, ChatChannel> {

        public FromStringConverter() {
        }

        @Override
        public ChatChannel convertLoad(String name) {
            return ChatChannel.valueOf(name);
        }

        @Override
        public String convertSave(ChatChannel channel) {
            return channel.getName();
        }
    }
}
