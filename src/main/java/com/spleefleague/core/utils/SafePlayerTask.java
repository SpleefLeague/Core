package com.spleefleague.core.utils;

import com.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author RINES <iam@kostya.sexy>
 */
public class SafePlayerTask {

    public static void call(Player p, SafePlayerTaskItself task) {
        if(p == null) {
            return;
        }
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
            if(!p.isOnline()) {
                return;
            }
            task.run(p);
        });
    }
    
    public static interface SafePlayerTaskItself {
        
        void run(Player p);
        
    }
    
}
