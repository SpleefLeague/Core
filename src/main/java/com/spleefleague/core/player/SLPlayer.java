package com.spleefleague.core.player;

import com.spleefleague.core.SpleefLeague;
import java.util.HashSet;
import java.util.UUID;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.TypeConverter.HashSetIntegerConverter;
import com.spleefleague.core.io.TypeConverter.HashSetStringConverter;
import com.spleefleague.core.io.TypeConverter.RankStringConverter;
import com.spleefleague.core.io.TypeConverter.UUIDStringConverter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
    
    public void resetVisibility() {
        for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
            if(slp != this && this.getState() == PlayerState.IDLE) {
                if(slp.getState() == PlayerState.IDLE) {
                    this.showPlayer(slp);
                    slp.showPlayer(this);
                }
                else {
                    this.hidePlayer(slp);
                    slp.hidePlayer(this);
                }
            }
        }
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
