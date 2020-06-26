/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBLoad;
import com.spleefleague.entitybuilder.DBLoadable;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jonas
 */
public class PlayerOptions extends DBEntity implements DBLoadable, DBSaveable {

    @DBLoad(fieldName = "disabledChannels", typeConverter = ChatChannel.FromStringConverter.class)
    @DBSave(fieldName = "disabledChannels", typeConverter = ChatChannel.FromStringConverter.class)
    protected Set<ChatChannel> disabledChannels;
    
    @DBLoad(fieldName = "enabledChannels", typeConverter = ChatChannel.FromStringConverter.class)
    @DBSave(fieldName = "enabledChannels", typeConverter = ChatChannel.FromStringConverter.class)
    protected Set<ChatChannel> enabledChannels;

    @DBLoad(fieldName = "visibilityMode")
    @DBSave(fieldName = "visibilityMode")
    private VisibilityMode visibilityMode;

    @DBLoad(fieldName = "parkourRecordGhost.type")
    @DBSave(fieldName = "parkourRecordGhost.type")
    private ParkourRecordGhostType parkourRecordGhostType = ParkourRecordGhostType.NONE;
    @DBLoad(fieldName = "parkourRecordGhost.offset")
    @DBSave(fieldName = "parkourRecordGhost.offset")
    private int parkourRecordGhostOffset = 0;

    protected void apply(SLPlayer slp) {
        setChatChannels(slp);
        setVisibility(slp);
    }

    private void setChatChannels(SLPlayer slp) {
        HashSet<ChatChannel> channels = new HashSet<>();
        for (ChatChannel channel : ChatManager.getAvailableChatChannels(slp)) {
            if (!channel.isDefault() && enabledChannels.contains(channel)) {
                channels.add(channel);
            } else if (!disabledChannels.contains(channel)) {
                channels.add(channel);
            }
        }
        slp.setReceivingChatChannels(channels);
    }

    public ParkourRecordGhostType getParkourRecordGhostType() {
        return parkourRecordGhostType;
    }

    public int getParkourRecordGhostOffset() {
        return parkourRecordGhostOffset;
    }

    public void setParkourRecordGhostType(ParkourRecordGhostType parkourRecordGhostType) {
        this.parkourRecordGhostType = parkourRecordGhostType;
    }

    public void setParkourRecordGhostOffset(int parkourRecordGhostOffset) {
        this.parkourRecordGhostOffset = parkourRecordGhostOffset;
    }

    public void disableChatChannel(ChatChannel channel) {
        if (channel.isDefault()) {
            disabledChannels.add(channel);
        } else {
            enabledChannels.remove(channel);
        }
    }

    public void enableChatChannel(ChatChannel channel) {
        if (channel.isDefault()) {
            disabledChannels.remove(channel);
        } else {
            enabledChannels.add(channel);
        }
    }

    public boolean isChatChannelEnabled(ChatChannel channel) {
        if (channel.isDefault()) {
            return !disabledChannels.contains(channel);
        } else {
            return enabledChannels.contains(channel);
        }
    }

    private void setVisibility(SLPlayer slp) {

    }

    public static PlayerOptions getDefault() {
        PlayerOptions po = new PlayerOptions();
        po.disabledChannels = new HashSet<>();
        po.enabledChannels = new HashSet<>();
        po.visibilityMode = VisibilityMode.DEFAULT;
        po.parkourRecordGhostType = ParkourRecordGhostType.NONE;
        return po;
    }

    public static enum VisibilityMode {
        ALL,
        NONE,
        FRIENDS,
        DEFAULT;
    }

    public static enum ParkourRecordGhostType {
        NONE,
        OWN,
        BEST;
    }
}
