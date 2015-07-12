/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.queue;

import net.spleefleague.core.player.GeneralPlayer;

/**
 *
 * @author Jonas
 */
public interface Queue {
    public boolean isOccupied();
    public boolean isQueued(GeneralPlayer gp);
    public boolean isAvailable(GeneralPlayer gp);
    public int getSize();
    public int getQueueLength();
    public int getQueuePosition(GeneralPlayer gp);
    public String getName();
    public String getCurrentState();
}
