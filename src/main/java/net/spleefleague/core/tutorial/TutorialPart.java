/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.tutorial;

import net.spleefleague.core.player.SLPlayer;

/**
 *
 * @author Jonas
 */
public abstract class TutorialPart {
    
    private final SLPlayer slp;
    
    public TutorialPart(SLPlayer slp) {
        this.slp = slp;
    }
    
    public SLPlayer getPlayer() {
        return slp;
    }
    
    public abstract void onComplete();
    public abstract void onCancel();
    public abstract void start();
}
