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
public class GameQueue<P extends GeneralPlayer> {
        
    private static final String DEFAULT = "DEFAULT";
    private final Map<String, Queue<P>> queues = new HashMap<>();
    private final Queue<P> all = new LinkedList<>();
    
    public GameQueue() {
        queues.put(DEFAULT, new LinkedList<P>());
    }
    
    public void register(String queue) {
        if(getQueue(queue) != null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" already exists!");
        }
        queues.put(queue, new LinkedList<P>());
    }
    
    public void unregister(String queue) {
        if(queue.equals(DEFAULT)) {
            throw new UnsupportedOperationException("Default queue can't be removed!");
        }
        Queue<P> q = queues.get(queue);
        queues.remove(queue);
        while(!q.isEmpty()) {
            dequeue(q.poll());
        }
    }
    
    public void queue(P player, String queue) {
        Queue<P> q = getQueue(queue);
        if(isQueued(player)) {
            dequeue(player);
        }
        if(q == null) {
            throw new UnsupportedOperationException("Queue \"" + queue + "\" does not exists!");
        }
        else {
            q.add(player);
            all.add(player);
        }
    }
    
    public void queue(P player) {
        queue(player, DEFAULT);
    }
    
    public void dequeue(GeneralPlayer player) {
        all.remove(player);
        for(String name : queues.keySet()) {
            Queue<P> q = queues.get(name);
            q.remove(player);
        }
    }
    
    public boolean isQueued(GeneralPlayer player) {
        return all.contains(player);
    }
    
    public Collection<P> request(String requestedQueue, int amount) {
        Queue<P> r = getQueue(requestedQueue);
        Queue<P> d = getQueue(DEFAULT);
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
    
    private Queue<P> getQueue(String name) {
        return queues.get(name);
    }
}
