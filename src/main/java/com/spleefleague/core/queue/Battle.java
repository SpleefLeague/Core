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

}
