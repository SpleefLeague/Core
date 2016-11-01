/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.player.GeneralPlayer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 * @param <B>
 */
public abstract class BattleManager<Q extends QueueableArena, P extends GeneralPlayer, B extends Battle<Q, P>> {

    private final HashSet<B> activeBattles;
    private GameQueue<Q, P> gameQueue;

    public BattleManager(GameQueue<Q, P> gameQueue) {
        this.activeBattles = new HashSet<>();
        this.gameQueue = gameQueue;
    }

    protected void setGameQueue(GameQueue<Q, P> gameQueue) {
        this.gameQueue = gameQueue;
    }

    public GameQueue<Q, P> getGameQueue() {
        return gameQueue;
    }

    public void registerArena(Q arena) {
        gameQueue.registerArena(arena);
    }

    public void unregisterArena(Q arena) {
        gameQueue.unregisterArena(arena);
    }

    public void queue(P player, Q queue) {
        gameQueue.queuePlayer(player, queue);
    }

    public void queue(P player) {
        gameQueue.queuePlayer(player);
    }

    public void dequeue(P sjp) {
        gameQueue.dequeuePlayer(sjp);
    }

    public boolean isQueued(P sjp) {
        return gameQueue.isQueued(sjp);
    }

    public void add(B battle) {
        activeBattles.add(battle);
    }

    public void remove(B battle) {
        activeBattles.remove(battle);
    }

    public Collection<B> getAll() {
        return activeBattles;
    }

    public B getBattle(P player) {
        for (B battle : activeBattles) {
            for (P p : battle.getActivePlayers()) {
                if (player == p) {
                    return battle;
                }
            }
        }
        return null;
    }

    public B getBattleForSpectator(P player) {
        for (B battle : activeBattles) {
            for (P p : battle.getSpectators()) {
                if (player == p) {
                    return battle;
                }
            }
        }
        return null;
    }

    public B getBattle(Q arena) {
        for (B battle : activeBattles) {
            if (battle.getArena() == arena) {
                return battle;
            }
        }
        return null;
    }

    public boolean isIngame(P p) {
        return getBattle(p) != null;
    }

    public abstract void startBattle(Q queue, List<P> players);
}
