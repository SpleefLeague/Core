/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.plugin;

import net.spleefleague.core.player.GeneralPlayer;
import net.spleefleague.core.queue.GameQueue;
import net.spleefleague.core.queue.Queue;

/**
 *
 * @author Jonas
 * @param <P>
 * @param <Q>
 */
public abstract class QueueableCoreGame<P extends GeneralPlayer, Q extends Queue> extends CoreGame {
    
    private final GameQueue<P, Q> queue;
    
    public QueueableCoreGame(String prefix, String chatPrefix) {
        super(prefix, chatPrefix);
        queue = new GameQueue();
    }
    
    public GameQueue<P, Q> getGameQueue() {
        return queue;
    }
    
    public boolean isInQueue(GeneralPlayer gp) {
        return queue.isQueued(gp);
    }
    
    public void dequeue(GeneralPlayer gp) {
        queue.dequeue(gp);
    }
}
