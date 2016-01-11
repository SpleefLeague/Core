package com.spleefleague.core.player;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import java.util.HashSet;
import java.util.UUID;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBSave;
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
    private HashSet<ChatChannel> chatChannels;
    private ChatChannel sendingChannel;
    private PlayerState state = PlayerState.IDLE;
    private PlayerOptions options;
    private boolean hasForumAccount = false;
    
    public SLPlayer() {
        super();
        this.chatChannels = new HashSet<>();
        this.sendingChannel = ChatChannel.GLOBAL;
    }
    
    @DBSave(fieldName = "rank", typeConverter = RankStringConverter.class)
    public Rank getRank() {
        return rank;
    }
    
    @DBLoad(fieldName = "rank", typeConverter = RankStringConverter.class)
    public void setRank(final Rank rank) {
        this.rank = rank;
        if(isOnline()) {
            setPlayerListName(rank.getColor() + getName());
            setDisplayName(rank.getColor() + getName());
            if(rank.hasPermission(Rank.DEVELOPER)) {
                setGameMode(GameMode.CREATIVE);
            }
            else {
                setGameMode(GameMode.SURVIVAL);
            }
            rank.managePermissions(this);
        }
    }
    
    @DBLoad(fieldName = "coins")
    public void setCoins(int coins) {
        this.coins = coins;
    }
    
    @DBSave(fieldName = "coins")
    public int getCoins() {
        return coins;
    }
    
    @DBSave(fieldName = "lastChatPartner", typeConverter = UUIDStringConverter.class)
    public UUID getLastChatPartner() {
        return lastChatPartner;
    }
    
    @DBLoad(fieldName = "lastChatPartner", typeConverter = UUIDStringConverter.class)
    public void setLastChatPartner(UUID lastChatPartner) {
        this.lastChatPartner = lastChatPartner;
    }
    
    @DBSave(fieldName = "options")
    public PlayerOptions getOptions() {
        return options;
    }
    
    @DBLoad(fieldName = "options", priority = -100)
    private void setOptions(PlayerOptions options) {
        this.options = options;
        options.apply(this);
    }
    
    protected void setReceivingChatChannels(HashSet<ChatChannel> chatChannels) {
        this.chatChannels = chatChannels;
    }
    
    public void setSendingChannel(ChatChannel channel) {
        this.sendingChannel = channel;
    }
    
    public ChatChannel getSendingChannel() {
        return sendingChannel;
    }
    
    public boolean isInChatChannel(ChatChannel channel) {
        return chatChannels.contains(channel);
    }
    
    public void addChatChannel(ChatChannel channel) {
        this.chatChannels.add(channel);
    }
    
    public void removeChatChannel(ChatChannel channel) {
        this.chatChannels.remove(channel);
    }
    
    public void setState(PlayerState state) {
        this.state = state;
    }
    
    public PlayerState getState() {
        return state;
    }    
    
    @DBLoad(fieldName = "hasForumAccount")
    public void setForumAccount(boolean forumAccount) {
        this.hasForumAccount = forumAccount;
    }

    @DBSave(fieldName = "hasForumAccount")
    public boolean hasForumAccount() {
        return hasForumAccount;
    }
    
    public void resetVisibility() {
        if(this.getPlayer() != null && this.getPlayer().isOnline()) {
            for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                if(slp != this && this.getState() == PlayerState.IDLE) {
                    if(slp.getState() == PlayerState.IDLE) {
                        this.showPlayer(slp);
                        slp.showPlayer(this);
                    }
                    else {
                        this.showPlayer(slp);
                        slp.showPlayer(this);
                    }
                }
            }
        }
    }
    
    @Override
    public void done() {
        if(this.options == null) {
            this.options = PlayerOptions.getDefault();
            this.options.apply(this);
        }
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        setRank(Rank.DEFAULT);
        setCoins(0);
        this.chatChannels.clear();
        this.chatChannels.add(ChatChannel.GLOBAL);
        setSendingChannel(ChatChannel.GLOBAL);
    }
}
