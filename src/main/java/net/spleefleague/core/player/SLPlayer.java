/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.utils.TypeConverter;

/**
 *
 * @author Jonas
 */
public class SLPlayer extends GeneralPlayer {
    
    private Rank rank;
    
    public SLPlayer() {
        
    }
    
    public Rank getRank() {
        return rank;
    }
    
    @DBLoad(fieldName = "rank")
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @Override
    public void setDefaults() {
        this.rank = Rank.DEFAULT;
    }
}