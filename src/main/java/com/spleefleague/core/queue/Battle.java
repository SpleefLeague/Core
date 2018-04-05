/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.player.GeneralPlayer;
import java.util.Collection;

/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 */
public interface Battle<Q extends QueueableArena, P extends GeneralPlayer> {

    Collection<P> getSpectators();

    Collection<P> getActivePlayers();

    Q getArena();

    Collection<P> getPlayers();
    
    /**
     * Calculates the rating change according to modified
     * version of this formula 
     * https://en.wikipedia.org/wiki/Elo_rating_system
     * 
     * @param p1 The rating of player 1
     * @param p2 The rating of player 2
     * @param compare -1 if p1 won, 0 if draw, 1 if p2 won
     * @return The rating change from the position of p1, negate for p2
    */
    public static double calculateEloRatingChange(double p1, double p2, int compare) {
        final double MAX_RATING = 40;
        double elo = (1f / (1f + Math.pow(2f, ((p2 - p1) / 250f))));
        double s;
        if(compare < 0) {
            s = 1;
        }
        else if(compare > 0) {
            s = 0;
        }
        else {
            s = 0.5;
        }
        double ratingChange = MAX_RATING * (s - elo);
        if(compare != 0) {
            double sign = Math.signum(ratingChange);
            ratingChange = sign * Math.abs(ratingChange);
        }
        return ratingChange;
    }
}
