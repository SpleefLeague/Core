/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.plugin;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.PlayerEndedSpectatingEvent;
import com.spleefleague.core.events.PlayerStartedSpectatingEvent;
import com.spleefleague.core.queue.Battle;
import com.spleefleague.core.queue.BattleManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Bukkit;

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
    
    public boolean spectateGracefully(Player target, Player p) {
        boolean result = spectate(target, p);
        if(result)
            Bukkit.getPluginManager().callEvent(new PlayerStartedSpectatingEvent(p, target, this));
        return result;
    }
    
    public void unspectateGracefully(Player p) {
        if(!isSpectating(p))
            return;
        Battle battle = getBattleManager().getBattleForSpectator(SpleefLeague.getInstance().getPlayerManager().get(p));
        unspectate(p);
        Bukkit.getPluginManager().callEvent(new PlayerEndedSpectatingEvent(p, battle));
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

    public abstract BattleManager getBattleManager();
    
    private String getImplVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        if(version == null)
            return "unknown";
        return version;
    }
    
    public String getCommitId() {
        String version = getImplVersion();
        if(version.equals("unknown"))
            return version;
        return version.split("\\-")[0];
    }
    
    public String getCommitDate() {
        String version = getImplVersion();
        if(version.equals("unknown"))
            return "";
        return version.split("\\-")[1];
    }

    public static void dequeueGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            gp.dequeue(p);
        }
    }

    public static void cancelGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            gp.cancel(p);
        }
    }

    public static void surrenderGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            gp.surrender(p);
        }
    }

    public static void cancelAllMatches() {
        for (GamePlugin gp : gamePlugins) {
            gp.cancelAll();
        }
    }

    public static void setQueueStatusGlobal(boolean open) {
        for (GamePlugin gp : gamePlugins) {
            gp.setQueueStatus(open);
        }
    }

    public static void unspectateGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            gp.unspectateGracefully(p);
        }
    }

    public static boolean isQueuedGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            if (gp.isQueued(p)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIngameGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            if (gp.isIngame(p)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSpectatingGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            if (gp.isSpectating(p)) {
                return true;
            }
        }
        return false;
    }

    public static void requestEndgameGlobal(Player p) {
        for (GamePlugin gp : gamePlugins) {
            gp.requestEndgame(p);
        }
    }

    public static void printAllStats(Player p) {
        for (GamePlugin gp : gamePlugins) {
            gp.printStats(p);
        }
    }

    public static Collection<GamePlugin> getGamePlugins() {
        return gamePlugins;
    }
}
