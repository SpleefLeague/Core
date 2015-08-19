/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import com.spleefleague.core.player.GeneralPlayer;

/**
 *
 * @author Jonas
 * @param <P>
 * @param <Q>
 */
public class GameQueue<P extends GeneralPlayer, Q extends QueueableArena> {
        
    private final Map<Q, Queue<P>> queues = new HashMap<>();
    private final Queue<P> all = new LinkedList<>();
    
    public GameQueue() {
        queues.put(null, new LinkedList<>());
    }
    
    public Set<Q> getQueues() {
        return queues.keySet();
    }
    
    public QueueableArena getGeneralQueue() {
        QueueableArena queue = new QueueableArena() {

            private Queue<P> q = getQueue(null);
            
            @Override
            public boolean isOccupied() {
                return false;
            }
            
            @Override
            public boolean isAvailable(GeneralPlayer gp) {
                return true;
            }

            @Override
            public int getSize() {
                return -1;
            }

            @Override
            public int getQueueLength() {
                return q.size();
            }

            @Override
            public String getName() {
                return "General queue";
            }

            @Override
            public String getCurrentState() {
                return null;
            }

            @Override
            public int getQueuePosition(GeneralPlayer gp) {
                int i = 1;
                for(GeneralPlayer p : q) {
                    if(p == gp) break;
                    i++;
                }
                return i;
            }

            @Override
            public boolean isInGeneral() {
                return true;
            }

            @Override
            public boolean isPaused() {
                return false;
            }
        };
        return queue;
    }
    
    public void register(Q queue) {
        if(getQueue(queue) != null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" already exists!");
        }
        queues.put(queue, new LinkedList<>());
    }
    
    public void unregister(Q queue) {
        if(queue == null) {
            throw new UnsupportedOperationException("Default queue can't be removed!");
        }
        Queue<P> q = queues.get(queue);
        if(q == null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" doesn't exist!");
        }
        queues.remove(queue);
        while(!q.isEmpty()) {
            dequeue(q.poll());
        }
    }
    
    public void queue(P player, Q queue) {
        Queue<P> q = getQueue(queue);
        if(isQueued(player)) {
            dequeue(player);
        }
        if(q == null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" does not exists!");
        }
        else {
            q.add(player);
            if(queue == null || queue.isInGeneral()) {
                all.add(player);
            }
        }
    }
    
    public void queue(P player) {
        queue(player, null);
    }
    
    public void dequeue(P player) {
        all.remove(player);
        for(Q queue : queues.keySet()) {
            Queue<P> q = queues.get(queue);
            q.remove(player);
        }
    }
    
    public boolean isQueued(P player) {
        return all.contains(player);
    }
    
    public HashMap<Q, Collection<P>> request() {
        HashMap<Q, Collection<P>> requested = new HashMap<>();
        for(Q q : queues.keySet()) {
            if(q != null) {
                if(q.isOccupied() || q.isPaused()) continue;
                Collection<P> players = request(q);
                if(players != null) {
                    requested.put(q, players);
                }
            }
        }
        return requested;
    }
    
    //Will return players even when queue is occupied
    public Collection<P> request(Q requestedQueue) {
        if(requestedQueue.isPaused()) return null;
        int amount = requestedQueue.getSize();
        Queue<P> r = getQueue(requestedQueue);
        if(requestedQueue.isInGeneral()) {
            Queue<P> d = getQueue(null);
            P[] array = (P[])all.toArray(new GeneralPlayer[0]);
            Collection<P> result = new ArrayList<>();
            for(int i = 0; i < array.length && amount > 0; i++) {
                P q = array[i];
                if(r.contains(q) || d.contains(q) && requestedQueue.isAvailable(q)) {
                    result.add(q);
                    amount--;
                }
            }
            if(amount == 0) {
                for(P q : result) {
                    dequeue(q);
                }
                return result;
            }
            else {
                return null;
            }
        }
        else {
            Collection<P> result = new ArrayList<>();
            if(r.size() >= amount) {
                while(result.size() < amount) {
                    result.add(r.poll());
                }
                return result;
            }
            else {
                return null;
            }
        }
    }
    
    public int getQueueLength(Q name) {
        return getQueue(name).size();
    }
    
    public int getQueuePosition(Q name, P player) {
        int i = 1;
        for(P p : getQueue(name)) {
            if(p == player) break;
            i++;
        }
        return i;
    }
    
    private Queue<P> getQueue(Q name) {
        return queues.get(name);
    }
}
