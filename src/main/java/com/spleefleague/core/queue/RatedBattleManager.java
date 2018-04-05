/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.player.GeneralPlayer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 * @param <B>
 */
public class RatedBattleManager<Q extends QueueableArena, P extends GeneralPlayer, B extends Battle<Q, P>> extends BattleManager<Q, P, B> {

    public RatedBattleManager(Consumer<RatedGameQueue<Q,P>.Match> matchConsumer, Function<P, Integer> ratingFunction) {
        super(new RatedGameQueue<Q, P>(matchConsumer, ratingFunction));
    }

    public RatedBattleManager(RatedGameQueue<Q, P> gameQueue) {
        super(gameQueue);
    }
}
