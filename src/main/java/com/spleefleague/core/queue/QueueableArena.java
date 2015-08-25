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
 */
public interface QueueableArena {
    public boolean isOccupied();
    public boolean isAvailable(UUID gp);
    public int getSize();
    public int getQueueLength();
    public int getQueuePosition(UUID gp);
    public String getName();
    public String getCurrentState();
    public boolean isInGeneral();
    public boolean isPaused();
}
