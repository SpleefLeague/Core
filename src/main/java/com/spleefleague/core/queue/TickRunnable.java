/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Jonas
 */
public abstract class TickRunnable extends BukkitRunnable {

    private int tick = 0;

    public TickRunnable() {

    }

    @Override
    public void run() {
        tick++;
    }

    public int getTick() {
        return tick;
    }

    public void resetTickCounter() {
        this.tick = 0;
    }
}
