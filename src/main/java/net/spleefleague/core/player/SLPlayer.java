/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import java.util.UUID;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;

/**
 *
 * @author Jonas
 */
public class SLPlayer extends GeneralPlayer {
    
    private Rank rank;

    public SLPlayer() {
        super();
    }
    
    public SLPlayer(UUID uuid, String username) {
        super(uuid, username);
    }
    
    @DBSave(fieldName = "rank")
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