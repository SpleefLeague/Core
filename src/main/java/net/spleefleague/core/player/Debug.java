/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import net.spleefleague.core.utils.Debugger;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.RegisteredListener;

/**
 *
 * @author Jonas
 */
public class Debug implements Debugger{

    @Override
    public void debug() {
        for(RegisteredListener rl : EntityDamageEvent.getHandlerList().getRegisteredListeners()) {
            if(!rl.getListener().getClass().getName().contains("spleefleague")) {
                EntityDamageByEntityEvent.getHandlerList().unregister(rl.getListener());
            }
        }
    }
}
