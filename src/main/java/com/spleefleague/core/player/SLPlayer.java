package com.spleefleague.core.player;

import java.util.HashSet;
import java.util.UUID;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.TypeConverter.HashSetIntegerConverter;
import com.spleefleague.core.io.TypeConverter.HashSetStringConverter;
import com.spleefleague.core.io.TypeConverter.RankStringConverter;
import com.spleefleague.core.io.TypeConverter.UUIDStringConverter;
import org.bukkit.GameMode;

/**
 *
 * @author Jonas
 */
public class SLPlayer extends GeneralPlayer {
    
    private Rank rank;
    private UUID lastChatPartner;
    private int coins;
    private HashSet<String> chatChannels;
    @DBLoad(fieldName = "easteregg", typeConverter = HashSetIntegerConverter.class)
    private HashSet<Integer> eastereggs;
    private String sendingChannel;
    private PlayerState state = PlayerState.IDLE;
    
    public SLPlayer() {
        super();
        chatChannels = new HashSet<>();
        eastereggs = new HashSet<>();
    }
    
    @DBSave(fieldName = "rank", typeConverter = RankStringConverter.class)
    public Rank getRank() {
        return rank;
    }
    
    @DBLoad(fieldName = "rank", typeConverter = RankStringConverter.class)
    public void setRank(final Rank rank) {
        this.rank = rank;
        getPlayer().setPlayerListName(rank.getColor() + getName());
        if(rank.hasPermission(Rank.DEVELOPER)) {
            getPlayer().setGameMode(GameMode.CREATIVE);
        }
        else {
            getPlayer().setGameMode(GameMode.SURVIVAL);
        }
        rank.managePermissions(getPlayer());
    }
    
    @DBLoad(fieldName = "coins")
    public void setCoins(int coins) {
        this.coins = coins;
    }
    
    @DBSave(fieldName = "coins")
    public int getCoins() {
        return coins;
    }
    
    @DBSave(fieldName = "easteregg", typeConverter = HashSetIntegerConverter.class)
    public HashSet<Integer> getEastereggs() {
        return eastereggs;
    }
    
    @DBSave(fieldName = "lastChatPartner", typeConverter = UUIDStringConverter.class)
    public UUID getLastChatPartner() {
        return lastChatPartner;
    }
    
    @DBLoad(fieldName = "lastChatPartner", typeConverter = UUIDStringConverter.class)
    public void setLastChatPartner(UUID lastChatPartner) {
        this.lastChatPartner = lastChatPartner;
    }
    
    @DBLoad(fieldName = "chatChannels", typeConverter = HashSetStringConverter.class)
    public void setReceivingChatChannels(HashSet<String> chatChannels) {
        this.chatChannels = chatChannels;
    }
    
    @DBSave(fieldName = "chatChannels", typeConverter = HashSetStringConverter.class)
    public HashSet<String> getReceivingChatChannels() {
        return chatChannels;
    }
    
    @DBLoad(fieldName = "sendingChannel")
    public void setSendingChannel(String channel) {
        this.sendingChannel = channel;
    }
    
    @DBSave(fieldName = "sendingChannel")
    public String getSendingChannel() {
        return sendingChannel;
    }
    
    public boolean isInChatChannel(String channel) {
        return chatChannels.contains(channel);
    }
    
    public void addChatChannel(String channel) {
        this.chatChannels.add(channel);
    }
    
    public void removeChatChannel(String channel) {
        this.chatChannels.remove(channel);
    }
    
    public void setState(PlayerState state) {
        this.state = state;
    }
    
    public PlayerState getState() {
        return state;
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        setRank(Rank.DEFAULT);
        setCoins(0);
        this.chatChannels.clear();
        this.eastereggs.clear();
        this.chatChannels.add("DEFAULT");
        setSendingChannel("DEFAULT");
    }
}
