/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;


/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 * @param <B>
 */
public abstract class RatedBattleManager<Q extends QueueableArena, P extends RatedPlayer, B extends Battle<Q, P>> extends BattleManager<Q, P, B> {
    
    public RatedBattleManager() {
        super(null);
        this.setGameQueue(new RatedGameQueue<>(this));
    }
    
    public RatedBattleManager(RatedGameQueue<Q, P> gameQueue) {
        super(gameQueue);
    }
}