/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.tutorial;

import java.util.Random;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.chat.Theme;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Jonas
 */
public abstract class TutorialPart {
    
    private final SLPlayer slp;
    protected static final Random random = new Random();
    protected int currentStep = 0;
    protected final Tutorial tutorial;
    
    public TutorialPart(SLPlayer slp, Tutorial tutorial) {
        this.slp = slp;
        this.tutorial = tutorial;
    }
    
    public SLPlayer getPlayer() {
        return slp;
    }
    
    protected void sendMessages(final String[] messages, final boolean increaseStep) {
        BukkitRunnable br = new BukkitRunnable() {
            private int tick = 0;
            @Override
            public void run() {
                if(messages.length <= tick) {
                    if(increaseStep) {
                        currentStep++;
                    }
                    super.cancel();
                }
                else {
                    getPlayer().getPlayer().sendMessage(Theme.INFO + messages[tick]);
                    tick++;
                }
            }
        };
        br.runTaskTimer(SpleefLeague.getInstance(), 20, 60);
    }
    
    protected void moveVillager(Location to) {
        
    }
    
    public abstract void onComplete();
    public abstract void onCancel();
    public abstract void start();
    public void onPlayerMessage(String message) {
        //Do nothing
    }
}
