package net.spleefleague.core.player;

import com.mongodb.BasicDBList;
import java.util.HashSet;
import java.util.UUID;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;
import net.spleefleague.core.utils.TypeConverter;
import net.spleefleague.core.utils.TypeConverter.UUIDStringConverter;

/**
 *
 * @author Jonas
 */
public class SLPlayer extends GeneralPlayer {
    
    private Rank rank;
    private UUID lastChatPartner;
    private int coins;
    private HashSet<String> chatChannels;
    private String sendingChannel;
    boolean hasCompletedTutorial;
    private PlayerState state = PlayerState.IDLE;
    
    public SLPlayer() {
        super();
        chatChannels = new HashSet<>();
    }
    
    @DBSave(fieldName = "rank")
    public Rank getRank() {
        return rank;
    }
    
    @DBLoad(fieldName = "rank")
    public void setRank(Rank rank) {
        this.rank = rank;
        if(rank.hasPermission(Rank.MODERATOR)) {
            chatChannels.add("STAFF");
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
    
    @DBLoad(fieldName = "chatChannels", typeConverter = HashSetConverter.class)
    public void setReceivingChatChannels(HashSet<String> chatChannels) {
        this.chatChannels = chatChannels;
    }
    
    @DBSave(fieldName = "chatChannels", typeConverter = HashSetConverter.class)
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
    
    @DBLoad(fieldName = "tutorialCompleted")
    public void setCompletedTutorial(boolean hasCompletedTutorial) {
        this.hasCompletedTutorial = hasCompletedTutorial;
    }
    
    @DBSave(fieldName = "tutorialCompleted")
    public boolean hasCompletedTutorial() {
        return this.hasCompletedTutorial;
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
        this.rank = Rank.DEFAULT;
        this.coins = 0;
        this.chatChannels.clear();
        this.chatChannels.add("DEFAULT");
        this.sendingChannel = "DEFAULT";
        this.hasCompletedTutorial = false;
    }

    public static class HashSetConverter extends TypeConverter<BasicDBList, HashSet<String>> {

        @Override
        public HashSet<String> convertLoad(BasicDBList t) {
            HashSet<String> hs = new HashSet<>();
            for (Object o : t) {
                hs.add((String) o);
            }
            return hs;
        }

        @Override
        public BasicDBList convertSave(HashSet<String> v) {
            BasicDBList bdbl = new BasicDBList();
            for (String s : v) {
                bdbl.add(s);
            }
            return bdbl;
        }
    }
}
