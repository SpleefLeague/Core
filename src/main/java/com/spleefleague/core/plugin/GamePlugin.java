/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.plugin;

import java.util.Collection;
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
    
    public abstract boolean spectate(Player target, Player p);
    public abstract void unspectate(Player p);
    public abstract void dequeue(Player p);
    public abstract void cancel(Player p);
    public abstract void surrender(Player p);
    public abstract void requestEndgame(Player p);
    public abstract void cancelAll();
    public abstract void setQueueStatus(boolean open);
    public abstract boolean isQueued(Player p);
    public abstract boolean isIngame(Player p);
    public abstract boolean isSpectating(Player p);
    public abstract void printStats(Player p);
    
    public static void dequeueGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.dequeue(p);
        }
    }
    
    public static void cancelGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.cancel(p);
        }
    }
    
    public static void surrenderGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.surrender(p);
        }
    }
    
    public static void cancelAllMatches() {
        for(GamePlugin gp : gamePlugins) {
            gp.cancelAll();
        }
    }
    
    public static void setQueueStatusGlobal(boolean open) {
        for(GamePlugin gp : gamePlugins) {
            gp.setQueueStatus(open);
        }
    }
    
    public static void unspectateGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.unspectate(p);
        }
    }
    
    public static boolean isQueuedGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
             if(gp.isQueued(p)) {
                 return true;
             }
        }
        return false;
    }
    
    public static boolean isIngameGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            if(gp.isIngame(p)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSpectatingGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            if(gp.isSpectating(p)) {
                return true;
            }
        }
        return false;
    }
    
    public static void requestEndgameGlobal(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.requestEndgame(p);
        }
    }
    
    public static void printAllStats(Player p) {
        for(GamePlugin gp : gamePlugins) {
            gp.printStats(p);
        }
    }
    
    public static Collection<GamePlugin> getGamePlugins() {
        return gamePlugins;
    }
}
