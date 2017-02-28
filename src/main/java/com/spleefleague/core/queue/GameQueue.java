/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.player.GeneralPlayer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Jonas
 * @param <Q>
 * @param <P>
 */
public abstract class GameQueue<Q extends QueueableArena, P extends GeneralPlayer> {

    private final Collection<Q> queuedArenas;
    private final Map<Q, Set<P>> queues;
    private final Map<Q, QueueMetadata> metadata;

    public GameQueue() {
        this.queuedArenas = new HashSet<>();
        this.queues = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    public void registerArena(Q arena) {
        if (arena.isQueued()) {
            this.queuedArenas.add(arena);
        }
        this.queues.put(arena, new HashSet<>());
        this.metadata.put(arena, new QueueMetadata());
    }

    public void unregisterArena(Q arena) {
        this.queuedArenas.remove(arena);
        this.queues.remove(arena);
        this.metadata.remove(arena);
    }

    public void queuePlayer(P player) {
        queuePlayer(player, null);
    }

    public void queuePlayer(P player, Q queue) {
        dequeuePlayer(player);
        this.queues.get(queue).add(player);
    }

    public void dequeuePlayer(P player) {
        for (Set<P> queue : queues.values()) {
            queue.remove(player);
        }
    }

    public boolean isQueued(P player) {
        for (Set<P> queue : queues.values()) {
            if (queue.contains(player)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Q> getRegisteredArenas() {
        return queuedArenas;
    }

    public Map<Q, Set<P>> getQueues() {
        return queues;
    }
    
    protected QueueMetadata getMetadata(Q q) {
        return metadata.get(q);
    }
    
    protected class QueueMetadata {
        
        public int skips = 0;
        public boolean skipped = false;
        
        public int getSkips() {
            return skips;
        }
        
        public void incrementSkips() {
            skips++;
        }
        
        public void resetSkips() {
            skips = 0;
        }
        
        public boolean isSkipped() {
            return skipped;
        }
        
        public void setSkipped(boolean skipped) {
            this.skipped = skipped;
        }
    }
}
