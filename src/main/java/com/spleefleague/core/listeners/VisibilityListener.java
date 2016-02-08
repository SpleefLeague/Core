package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * @author Josh Keighley
 */
public class VisibilityListener implements Listener {

    private static Listener instance;

    public static void init() {
        if(instance == null) {
            instance = new VisibilityListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private VisibilityListener() {

    }

    @Even

}
