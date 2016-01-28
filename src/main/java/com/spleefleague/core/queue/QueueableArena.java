/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import java.util.UUID;

/**
 *
 * @author Jonas
 * @param <P>
 */
public interface QueueableArena<P extends RatedPlayer> {
    
    public boolean isOccupied();
    public boolean isAvailable(P p);
    public int getSize();
    public String getName();
    public boolean isQueued();
    public boolean isPaused();
}
