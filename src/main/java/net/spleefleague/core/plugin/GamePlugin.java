/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.plugin;

import java.util.HashSet;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class GamePlugin extends CorePlugin {

    private static final HashSet<GamePlugin> gamePlugins = new HashSet<>();
    
    public GamePlugin(String prefix, String chatPrefix) {
        super(prefix, chatPrefix);
        gamePlugins.add(this);
    }
    
    public abstract void spectate(Player p);
    public abstract void dequeue(Player p);
    public abstract void cancel(Player p);
    public abstract boolean isQueued(Player p);
    public abstract boolean isIngame(Player p);
    
    public static void spectateAll(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.spectate(p);
        }
    }
    
    public static void dequeueAll(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.dequeue(p);
        }
    }
    
    public static void cancelAll(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.cancel(p);
        }
    }
    
    public static boolean isQueuedAll(Player p) {
        for(GamePlugin gp : gamePlugins) {
             if(gp.isQueued(p)) {
                 return true;
             }
        }
        return false;
    }
    
    public static boolean isIngameAll(Player p) {
        for(GamePlugin gp : gamePlugins) {
            if(gp.isIngame(p)) {
                return true;
            }
        }
        return false;
    }
}
