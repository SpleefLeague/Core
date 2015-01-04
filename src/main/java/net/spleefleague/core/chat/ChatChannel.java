/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.chat;

import net.spleefleague.core.player.Rank;

/**
 *
 * @author Jonas
 */
public class ChatChannel {
    
    private Rank minRank;
    private final String name, displayName;
    private final boolean defaultChannel;
    
    public ChatChannel(String name, String displayName, Rank minRank, boolean defaultChannel) {
        this.minRank = minRank;
        this.name = name;
        this.displayName = displayName;
        this.defaultChannel = defaultChannel;
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
}