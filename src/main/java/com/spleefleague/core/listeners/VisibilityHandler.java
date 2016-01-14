/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jonas
 */
public class VisibilityHandler {
    public static void hide(Player toHide, Player... players) {
        for(Player player : players) {
            player.hidePlayer(toHide);
        }
    }

    public static void hide(Player toHide, Collection<Player> players) {
        for(Player player : players) {
            player.hidePlayer(toHide);
        }
    }

    public static void show(Player toShow, Player... players) {
        for(Player player : players) {
            player.showPlayer(toShow);
        }
    }

    public static void show(Player toShow, List<Player> players) {
        for(Player player : players) {
            player.showPlayer(toShow);
        }
    }

}