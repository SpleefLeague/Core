/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.plugin;

import net.spleefleague.core.player.GeneralPlayer;

/**
 *
 * @author Jonas
 */
public abstract class CoreGame extends CorePlugin {

    public CoreGame(String prefix, String chatPrefix) {
        super(prefix, chatPrefix);
    }
    
    public abstract boolean isIngame(GeneralPlayer gp);
    public abstract void endGame(GeneralPlayer gp);
}
