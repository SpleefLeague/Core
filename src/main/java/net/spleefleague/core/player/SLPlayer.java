/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import java.util.UUID;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;
import net.spleefleague.core.utils.TypeConverter.UUIDStringConverter;

/**
 *
 * @author Jonas
 */
public class SLPlayer extends GeneralPlayer {
    
    private Rank rank;
    private UUID lastChatPartner;
    private int coins;
    
    public SLPlayer() {
        super();
    }
    
    @DBSave(fieldName = "rank")
    public Rank getRank() {
        return rank;
    }
    
    @DBLoad(fieldName = "rank")
    public void setRank(Rank rank) {
        this.rank = rank;
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

    @Override
    public void setDefaults() {
        this.rank = Rank.DEFAULT;
        this.coins = 0;
    }
}