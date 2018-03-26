/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.GeneralPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 */
public class RatedGameQueue<Q extends QueueableArena, P extends GeneralPlayer> extends GameQueue<Q, P> {

    private RatedBattleManager<Q, P, ? extends Battle> battleManager;
    private Function<P, Integer> ratingFunction;
    public static final int TICK_DURATION = 15 * 20;
    public static final int WAIT_TIME_INCOMPLETE_MATCH = 2;

    
    protected RatedGameQueue() {
        getQueues().put(null, new HashSet<>());
        gameQueues.add(this);
        if (tickTask == null) {
            tickTask = tickRunnable.runTaskTimer(SpleefLeague.getInstance(), 0, TICK_DURATION);
        }
    }

    public RatedGameQueue(RatedBattleManager<Q, P, ? extends Battle> battleHandler, Function<P, Integer> ratingFunction) {
        getQueues().put(null, new HashSet<>());
        this.battleManager = battleHandler;
        this.ratingFunction = ratingFunction;
        gameQueues.add(this);
        if (tickTask == null) {
            tickTask = tickRunnable.runTaskTimer(SpleefLeague.getInstance(), 0, TICK_DURATION);
        }
    }

    private void doTick() {
        Match match;
        while ((match = nextMatch(true)) != null) {
            for (P player : match.getPlayers()) {
                dequeuePlayer(player);
            }
            battleManager.startBattle(match.getQueue(), match.getPlayers());
        }
        while ((match = nextMatch(false)) != null) {
            for (P player : match.getPlayers()) {
                dequeuePlayer(player);
            }
            battleManager.startBattle(match.getQueue(), match.getPlayers());
        }
        getQueues().keySet().stream().filter((q) -> (q != null)).forEach((q) -> {
            this.getMetadata(q).setSkipped(false);
        });
    }

    private Match nextMatch(boolean forceFullTeam) {
        for (Entry<Q, Set<P>> entry : getQueues().entrySet()) {
            if (entry.getKey() == null || !entry.getKey().isPaused()) {
                for (P p1 : entry.getValue()) {
                    if (entry.getKey() != null) {
                        List<P> allowedPlayers = getAllowed(p1, entry.getKey());
                        QueueMetadata meta = this.getMetadata(entry.getKey());
                        if(forceFullTeam) {
                            if (allowedPlayers.size() >= entry.getKey().getSize() - 1) {
                                Collections.shuffle(allowedPlayers);
                                allowedPlayers = allowedPlayers.subList(0, entry.getKey().getSize() - 1);
                                allowedPlayers.add(p1);
                                meta.setSkipped(false);
                                meta.resetSkips();
                                return new Match(entry.getKey(), allowedPlayers);
                            }
                        }
                        else {
                            if (allowedPlayers.size() >= entry.getKey().getRequiredPlayers() - 1) {
                                if(!meta.isSkipped()) {
                                    meta.incrementSkips();
                                    meta.setSkipped(true);
                                }
                                if(meta.getSkips() >= WAIT_TIME_INCOMPLETE_MATCH) {
                                    meta.resetSkips();
                                    allowedPlayers.add(p1);
                                    return new Match(entry.getKey(), allowedPlayers);
                                }
                            }
                        }
                    } else {
                        List<Q> availableQueues = getRegisteredArenas().stream().filter((q) -> !q.isOccupied() && !q.isPaused() && q.isAvailable(p1)).collect(Collectors.toList());
                        Collections.shuffle(availableQueues);
                        for (Q queue : availableQueues) {
                            QueueMetadata meta = this.getMetadata(queue);
                            List<P> allowedPlayers = getAllowed(p1, queue);
                            if (forceFullTeam) {
                                if (allowedPlayers.size() >= queue.getSize() - 1) {
                                    meta.setSkipped(false);
                                    meta.resetSkips();
                                    Collections.shuffle(allowedPlayers);
                                    allowedPlayers = allowedPlayers.subList(0, queue.getSize() - 1);
                                    allowedPlayers.add(p1);
                                    return new Match(queue, allowedPlayers);
                                }
                            } 
                            else {
                                if (allowedPlayers.size() >= queue.getRequiredPlayers() - 1) {
                                    if(!meta.isSkipped()) {
                                        meta.incrementSkips();
                                        meta.setSkipped(true);
                                    }
                                    if(meta.getSkips() >= WAIT_TIME_INCOMPLETE_MATCH) {
                                        meta.resetSkips();
                                        allowedPlayers.add(p1);
                                        return new Match(queue, allowedPlayers);
                                    }
                                }
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
        allowedPlayers.addAll(getQueues().get(queue));
        if (queue != null) {
            allowedPlayers.addAll(getQueues().get(null));
        }
        allowedPlayers.remove(player);
        allowedPlayers = allowedPlayers
                .stream()
                .filter((p) -> queue == null || queue.isAvailable(p))
                .sorted((p1, p2) -> Double.compare(Math.abs(ratingFunction.apply(p1) - ratingFunction.apply(player)), Math.abs(ratingFunction.apply(p2) - ratingFunction.apply(player))))
                .limit(Math.min(allowedPlayers.size(), Math.max(Math.round(allowedPlayers.size() * MATCHMAKING_ACCURICY), MIN_AVAILABLE_PLAYERS)))
                .collect(Collectors.toList());
        return allowedPlayers;
    }

    private static final HashSet<RatedGameQueue> gameQueues;
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