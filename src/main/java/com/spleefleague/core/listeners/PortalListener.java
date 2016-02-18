package com.spleefleague.core.listeners;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.portals.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Josh on 15/02/2016.
 */
public class PortalListener implements Listener {

    private static Listener instance;

    public static void init() {
        if(instance == null) {
            instance = new PortalListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private PortalListener() {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
            return;
        }
        PortalManager.Portal portal = SpleefLeague.getInstance().getPortalManager().getByLocation(e.getTo());
        if(portal == null) {
            return;
        }
        e.getPlayer().teleport(portal.getTeleportTo());
        e.getPlayer().sendMessage(Theme.SUCCESS.buildTheme(true) + "Whoosh!");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) {
            return;
        }
        PortalManager.Portal portal = SpleefLeague.getInstance().getPortalManager().getByLocation(e.getClickedBlock().getLocation());
        if(portal == null) {
            return;
        }
        e.getPlayer().teleport(portal.getTeleportTo());
        e.getPlayer().sendMessage(Theme.SUCCESS.buildTheme(true) + "Whoosh!");
    }

}
