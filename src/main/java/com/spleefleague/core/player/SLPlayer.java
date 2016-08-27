package com.spleefleague.core.player;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.cosmetics.Collectibles;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.TypeConverter.RankStringConverter;
import com.spleefleague.core.io.TypeConverter.UUIDStringConverter;
import com.spleefleague.core.queue.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Jonas
 */
public class SLPlayer extends GeneralPlayer {

    private Rank rank;
    private UUID lastChatPartner;
    private int coins, premiumCredits;
    private HashSet<ChatChannel> chatChannels;
    private ChatChannel sendingChannel;
    private PlayerState state = PlayerState.IDLE;
    private PlayerOptions options;
    private Collectibles collectibles;
    private boolean hasForumAccount = false;
    private Map<UUID, Challenge> activeChallenges;
    private ChatColor chatArrowColor = ChatColor.DARK_GRAY;
    private String tabName = null;
    private long areaMessageCooldown = 0L;

    public SLPlayer() {
        super();
        this.chatChannels = new HashSet<>();
        this.activeChallenges = new HashMap<>();
        this.sendingChannel = ChatChannel.GLOBAL;
    }

    @DBSave(fieldName = "rank", typeConverter = RankStringConverter.class)
    public Rank getRank() {
        return rank;
    }

    @DBLoad(fieldName = "rank", typeConverter = RankStringConverter.class)
    public void setRank(final Rank rank) {
        this.rank = rank;
        if (isOnline()) {
            setPlayerListName(rank.getColor() + getName());
            setDisplayName(rank.getColor() + getName());
            for (Team t : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                t.removeEntry(getName());
            }
            getRank().getScoreboardTeam().addEntry(getName());
            if (rank.hasPermission(Rank.DEVELOPER)) {
                setGameMode(GameMode.CREATIVE);
            } else {
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
    
    public void changeCoins(int delta) {
        setCoins(coins + delta);
    }
    
    @DBLoad(fieldName = "premiumCredits")
    public void setPremiumCredits(int credits) {
        this.premiumCredits = credits;
    }
    
    @DBSave(fieldName = "premiumCredits")
    public int getPremiumCredits() {
        return premiumCredits;
    }
    
    public void changePremiumCredits(int delta) {
        setPremiumCredits(premiumCredits + delta);
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
    
    @DBSave(fieldName = "collectibles")
    public Collectibles getCollectibles() {
        return collectibles;
    }
    
    @DBLoad(fieldName = "collectibles", priority = -100)
    private void setCollectibles(Collectibles collectibles) {
        this.collectibles = collectibles;
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
        if (this.getPlayer() != null && this.getPlayer().isOnline()) {
            for (SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                if (slp != this && this.getState() == PlayerState.IDLE) {
                    if (slp.getState() == PlayerState.IDLE) {
                        this.showPlayer(slp);
                        slp.showPlayer(this);
                    }
                }
            }
        }
    }

    public void addChallenge(Challenge challenge) {
        activeChallenges.put(challenge.getChallengingPlayer().getUniqueId(), challenge);
    }

    public void removeChallenge(Challenge challenge) {
        activeChallenges.remove(challenge.getChallengingPlayer().getUniqueId());
    }

    public Challenge getChallenge(SLPlayer challenger) {
        return activeChallenges.get(challenger.getUniqueId());
    }

    @Override
    public void done() {
        if (this.options == null) {
            this.options = PlayerOptions.getDefault();
            this.options.apply(this);
        }
        if(this.collectibles == null) {
            this.collectibles = Collectibles.getDefault(this);
            this.collectibles.apply(this);
        }
    }

    @Override
    public void setDefaults() {
        super.setDefaults();
        setRank(Rank.DEFAULT);
        setCoins(0);
        setPremiumCredits(0);
        this.chatChannels.clear();
        this.chatChannels.add(ChatChannel.GLOBAL);
        setSendingChannel(ChatChannel.GLOBAL);
    }

    public void setChatArrowColor(ChatColor c) {
        this.chatArrowColor = c;
    }

    public ChatColor getChatArrowColor() {
        return this.chatArrowColor;
    }

    public void resetChatArrowColor() {
        this.chatArrowColor = ChatColor.DARK_GRAY;
    }

    public void setTabName(String s) {
        this.tabName = s;
        setPlayerListName(getTabName());
    }

    public String getTabName() {
        if (this.tabName == null) {
            return getRank().getColor() + getName();
        }
        return this.tabName;
    }

    public void updateAreaMessageCooldown() {
        this.areaMessageCooldown = System.currentTimeMillis();
    }

    public long getAreaMessageCooldown() {
        return areaMessageCooldown;
    }

}
