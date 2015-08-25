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
import com.spleefleague.core.player.PlayerManager;
import java.util.UUID;

/**
 *
 * @author Jonas
 * @param <Q>
 */
public class GameQueue<Q extends QueueableArena> {
        
    private final Map<Q, Queue<UUID>> queues = new HashMap<>();
    private final Queue<UUID> all = new LinkedList<>();
    
    public GameQueue() {
        queues.put(null, new LinkedList<>());
    }
    
    public Set<Q> getQueues() {
        return queues.keySet();
    }
    
    public QueueableArena getGeneralQueue() {
        QueueableArena queue = new QueueableArena() {

            private Queue<UUID> q = getQueue(null);
            
            @Override
            public boolean isOccupied() {
                return false;
            }
            
            @Override
            public boolean isAvailable(UUID uuid) {
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
            public int getQueuePosition(UUID uuid) {
                int i = 1;
                for(UUID u : q) {
                    if(u == uuid) break;
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
        Queue<UUID> q = queues.get(queue);
        if(q == null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" doesn't exist!");
        }
        queues.remove(queue);
        while(!q.isEmpty()) {
            dequeue(q.poll());
        }
    }
    
    public void queue(UUID player, Q queue) {
        Queue<UUID> q = getQueue(queue);
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
    
    public void queue(UUID player) {
        queue(player, null);
    }
    
    public void dequeue(UUID player) {
        all.remove(player);
        queues.keySet().stream().map((queue) -> queues.get(queue)).forEach((q) -> {
            q.remove(player);
        });
    }
    
    public boolean isQueued(UUID player) {
        return all.contains(player);
    }
    
    public <P extends GeneralPlayer> HashMap<Q, Collection<P>> request(PlayerManager<P> pm) {
        HashMap<Q, Collection<P>> requested = new HashMap<>();
        queues.keySet().stream().filter((q) -> (q != null)).filter((q) -> !(q.isOccupied() || q.isPaused())).forEach((q) -> {
            Collection<P> players = request(q, pm);
            if (players != null) {
                requested.put(q, players);
            }
        });
        return requested;
    }
    
    public HashMap<Q, Collection<UUID>> request() {
        HashMap<Q, Collection<UUID>> requested = new HashMap<>();
        queues.keySet().stream().filter((q) -> (q != null)).filter((q) -> !(q.isOccupied() || q.isPaused())).forEach((q) -> {
            Collection<UUID> players = request(q);
            if (players != null) {
                requested.put(q, players);
            }
        });
        return requested;
    }
    
    public <P extends GeneralPlayer> Collection<P> request(Q requestedQueue, PlayerManager<P> pm) {
        Collection<UUID> uuids = request(requestedQueue);
        if(uuids != null) {
            Collection<P> result = new ArrayList<>();
            uuids.stream().forEach((uuid) -> {
                result.add(pm.get(uuid));
            });
            return result;
        }
        return null;
    }
    
    //Will return players even when queue is occupied
    public Collection<UUID> request(Q requestedQueue) {
        if(requestedQueue.isPaused()) return null;
        int amount = requestedQueue.getSize();
        Queue<UUID> r = getQueue(requestedQueue);
        if(requestedQueue.isInGeneral()) {
            Queue<UUID> d = getQueue(null);
            UUID[] array = (UUID[])all.toArray(new UUID[all.size()]);
            Collection<UUID> result = new ArrayList<>();
            for(int i = 0; i < array.length && amount > 0; i++) {
                UUID q = array[i];
                if(r.contains(q) || d.contains(q) && requestedQueue.isAvailable(q)) {
                    result.add(q);
                    amount--;
                }
            }
            if(amount == 0) {
                result.stream().forEach((q) -> {
                    dequeue(q);
                });
                return result;
            }
            else {
                return null;
            }
        }
        else {
            Collection<UUID> result = new ArrayList<>();
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
    
    public int getQueuePosition(Q name, UUID player) {
        int i = 1;
        for(UUID p : getQueue(name)) {
            if(p == player) break;
            i++;
        }
        return i;
    }
    
    private Queue<UUID> getQueue(Q name) {
        return queues.get(name);
    }
}
