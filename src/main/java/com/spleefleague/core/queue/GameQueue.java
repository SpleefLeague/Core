/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.SpleefLeague;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 */
public class GameQueue<Q extends QueueableArena, P extends RatedPlayer> {
    
    private final BattleManager<Q, P, ? extends Battle> battleManager;
    private final List<Q> queuedArenas;
    private final Map<Q, Set<P>> queues;
    public static final int TICK_DURATION = 30 * 20;
    
    public GameQueue(BattleManager<Q, P, ? extends Battle> battleHandler) {
        this.battleManager = battleHandler;
        this.queuedArenas = new ArrayList<>();
        this.queues = new HashMap<>();
        this.queues.put(null, new HashSet<>());
        gameQueues.add(this);
        if(tickTask == null) {
            tickTask = tickRunnable.runTaskTimer(SpleefLeague.getInstance(), 0, TICK_DURATION);
        }
    }
    
    public void registerArena(Q arena) {
        if(arena.isQueued()) {  
            this.queuedArenas.add(arena);
        }
        this.queues.put(arena, new HashSet<>());
    }    
    
    public void unregisterArena(Q arena) {
        this.queuedArenas.remove(arena);
        this.queues.remove(arena);
    }
    
    public void queuePlayer(P player) {
        queuePlayer(player, null);
    }
    
    public void queuePlayer(P player, Q queue) {
        dequeuePlayer(player);
        this.queues.get(queue).add(player);
    }
    
    public void dequeuePlayer(P player) {
        for(Set<P> queue : queues.values()) {
            queue.remove(player);
        }
    }
    
    public boolean isQueued(P player) {
        for(Set<P> queue : queues.values()) {
            if(queue.contains(player)) {
                return true;
            }
        }
        return false;
    }
    
    private void doTick() {
        Match match;
        while((match = nextMatch()) != null) {
            for(P player : match.getPlayers()) {
                dequeuePlayer(player);
            }
            battleManager.startBattle(match.getQueue(), match.getPlayers());
        }
    }
    
    private Match nextMatch() {
        for(Entry<Q, Set<P>> entry : queues.entrySet()) {
            if(entry.getKey() == null || !entry.getKey().isPaused()) {
                for(P p1 : entry.getValue()) {
                    if(entry.getKey() != null) {
                        List<P> allowedPlayers = getAllowed(p1, entry.getKey());
                        if(allowedPlayers.size() >= entry.getKey().getSize() - 1) {
                            Collections.shuffle(allowedPlayers);
                            allowedPlayers = allowedPlayers.subList(0, entry.getKey().getSize() - 1);
                            allowedPlayers.add(p1);
                            return new Match(entry.getKey(), allowedPlayers);
                        }
                    }
                    else {
                        List<Q> availableQueues = queuedArenas.stream().filter((q) -> !q.isOccupied() && !q.isPaused() && q.isAvailable(p1)).collect(Collectors.toList());
                        Collections.shuffle(availableQueues);
                        for(Q queue : availableQueues) {
                            List<P> allowedPlayers = getAllowed(p1, queue);
                            if(allowedPlayers.size() >= queue.getSize() - 1) {
                                Collections.shuffle(allowedPlayers);
                                allowedPlayers = allowedPlayers.subList(0, queue.getSize() - 1);
                                allowedPlayers.add(p1);
                                return new Match(queue, allowedPlayers);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private List<P> getAllowed(P player, Q queue) {
        List<P> allowedPlayers = new ArrayList<>();
        allowedPlayers.addAll(queues.get(queue));
        if(queue != null) {
            allowedPlayers.addAll(queues.get(null));
        }
        allowedPlayers.remove(player);
        allowedPlayers = allowedPlayers.stream().filter((p) -> queue == null || queue.isAvailable(p)).sorted((p1, p2) -> Double.compare(Math.abs(p1.getRating() - player.getRating()), Math.abs(p2.getRating() - player.getRating()))).collect(Collectors.toList());
        allowedPlayers.subList(0, (int)Math.min(allowedPlayers.size(), Math.max(Math.round(allowedPlayers.size() * MATCHMAKING_ACCURICY), MIN_AVAILABLE_PLAYERS)));
        return allowedPlayers;
    }
    
    private Q getQueue(P p) {
        for(Entry<Q, Set<P>> entry : queues.entrySet()) {
            if(entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private static final HashSet<GameQueue> gameQueues;
    private static final BukkitRunnable tickRunnable;
    private static BukkitTask tickTask;
    private static final double MATCHMAKING_ACCURICY = 0.2;
    private static final int MIN_AVAILABLE_PLAYERS = 5;
    
    static {
        gameQueues = new HashSet<>();
        tickRunnable = new TickRunnable() {
            @Override
            public void run() {
                super.run();
                gameQueues.stream().forEach((queue) -> {
                    queue.doTick();
                });
            }
        };
    }
    private class Match {
        
        private final List<P> players;
        private final Q queue;
        
        public Match(Q queue, List<P> players) {
            this.players = players;
            this.queue = queue;
        }
        
        public List<P> getPlayers() {
            return players;
        }
        
        public Q getQueue() {
            return queue;
        }
    }
}
