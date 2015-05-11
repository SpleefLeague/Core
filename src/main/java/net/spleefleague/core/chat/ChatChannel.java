/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.chat;

import java.util.HashMap;
import java.util.Map;
import net.spleefleague.core.player.Rank;

/**
 *
 * @author Jonas
 */
public class ChatChannel {
    
    private Rank minRank;
    private final String name, displayName;
    private final boolean defaultChannel, temporary;
    
    public ChatChannel(String name, String displayName, Rank minRank, boolean defaultChannel) {
        this(name, displayName, minRank, defaultChannel, false);
    }
    
    public ChatChannel(String name, String displayName, Rank minRank, boolean defaultChannel, boolean temporary) {
        this.minRank = minRank;
        this.name = name;
        this.displayName = displayName;
        this.defaultChannel = defaultChannel;
        this.temporary = temporary;
        channels.put(name, this);
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
    
    //Could be used to disable global chat
    public void setMinRank(Rank minRank) {
        this.minRank = minRank;
    }
    
    public boolean isDefault() {
        return defaultChannel;
    }
    
    public boolean isTemporary() {
        return temporary;
    }
    
    private static Map<String, ChatChannel> channels = new HashMap<>();
    
    public static ChatChannel fromString(String channel) {
        return channels.get(channel);
    }
}