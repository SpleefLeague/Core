/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.plugin;

import net.spleefleague.core.player.GeneralPlayer;
import net.spleefleague.core.queue.GameQueue;

/**
 *
 * @author Jonas
 * @param <P>
 */
public abstract class QueueableCoreGame<P extends GeneralPlayer, K> extends CoreGame {
    
    private final GameQueue<P, K> queue;
    
    public QueueableCoreGame(String prefix, String chatPrefix) {
        super(prefix, chatPrefix);
        queue = new GameQueue();
    }
    
    public GameQueue<P, K> getGameQueue() {
        return queue;
    }
    
    public boolean isInQueue(GeneralPlayer gp) {
        return queue.isQueued(gp);
    }
    
    public void dequeue(GeneralPlayer gp) {
        queue.dequeue(gp);
    }
}
