/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import net.spleefleague.core.player.GeneralPlayer;

/**
 *
 * @author Jonas
 * @param <P>
 */
public class GameQueue<P extends GeneralPlayer, K> {
        
    private final Map<K, Queue<P>> queues = new HashMap<>();
    private final Queue<P> all = new LinkedList<>();
    
    public GameQueue() {
        queues.put(null, new LinkedList<P>());
    }
    
    public void register(K queue) {
        if(getQueue(queue) != null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" already exists!");
        }
        queues.put(queue, new LinkedList<P>());
    }
    
    public void unregister(K queue) {
        if(queue == null) {
            throw new UnsupportedOperationException("Default queue can't be removed!");
        }
        Queue<P> q = queues.get(queue);
        queues.remove(queue);
        while(!q.isEmpty()) {
            dequeue(q.poll());
        }
    }
    
    public void queue(P player, K queue) {
        queue(player, queue, true);
    }
    
    public void queue(P player, K queue, boolean general) {
        Queue<P> q = getQueue(queue);
        if(isQueued(player)) {
            dequeue(player);
        }
        if(q == null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" does not exists!");
        }
        else {
            q.add(player);
            if(general) all.add(player);
        }
    }
    
    public void queue(P player) {
        queue(player, null);
    }
    
    public void dequeue(GeneralPlayer player) {
        all.remove(player);
        for(K queue : queues.keySet()) {
            Queue<P> q = queues.get(queue);
            q.remove(player);
        }
    }
    
    public boolean isQueued(GeneralPlayer player) {
        return all.contains(player);
    }
    
    public Collection<P> request(K requestedQueue, int amount) {
        Queue<P> r = getQueue(requestedQueue);
        Queue<P> d = getQueue(null);
        P[] array = (P[])all.toArray();
        Collection<P> result = new ArrayList<>();
        for(int i = 0; i < array.length && amount > 0; i++) {
            P q = array[i];
            if(r.contains(q) || d.contains(q)) {
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
    
    private Queue<P> getQueue(K name) {
        return queues.get(name);
    }
}
