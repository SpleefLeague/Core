/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.events;

import net.spleefleague.core.player.GeneralPlayer;

/**
 *
 * @author Jonas
 */
public class PlayerQueueEvent extends QueueEvent {

    public PlayerQueueEvent(GeneralPlayer gp) {
        super(gp);
    }
}