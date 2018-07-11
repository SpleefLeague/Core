/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.io.typeconverters.RankConverter;
import com.spleefleague.entitybuilder.DBEntity;
import com.spleefleague.entitybuilder.DBLoad;
import com.spleefleague.entitybuilder.DBLoadable;
import com.spleefleague.entitybuilder.DBSave;
import com.spleefleague.entitybuilder.DBSaveable;

/**
 *
 * @author jonas
 */
public class TemporaryRank extends DBEntity implements DBLoadable, DBSaveable {
    
    @DBLoad(fieldName = "expirationTime")
    @DBSave(fieldName = "expirationTime")
    private long expirationTime;
    @DBLoad(fieldName = "rank", typeConverter = RankConverter.class)
    @DBSave(fieldName = "rank", typeConverter = RankConverter.class)
    private Rank rank;
    
    private TemporaryRank() {
        
    }
    
    public TemporaryRank(Rank rank, long expirationTime) {
        this.expirationTime = expirationTime;
        this.rank = rank;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public boolean isExpired() {
        return expirationTime <= System.currentTimeMillis();
    }
}
