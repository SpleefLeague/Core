/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.SLPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class Challenge {
    
    private final SLPlayer challenger;
    private boolean active = true;
    private int secondsLeft = 30;
    private final Collection<SLPlayer> accepted;
    private final int required;
    
    public Challenge(SLPlayer challenger, int required) {
        this.challenger = challenger;
        this.accepted = new ArrayList<>();
        this.accepted.add(challenger);
        this.required = required;
        challenges.add(this);
    }
    
    public void accept(SLPlayer player) {
        this.accepted.add(player);
        challenger.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + ChatColor.RED + player.getName() + ChatColor.GREEN + " has accepted your challenge.");
        if(accepted.size() == required) {
            active = false;
            start(accepted);
        }
    }
    
    public void decline(SLPlayer player) {
        active = false;
        for(SLPlayer slp : accepted) {
            slp.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + ChatColor.RED + player.getName() + " has declined the challenge.");
        }
    }
    
    public abstract void start(Collection<SLPlayer> accepted);
    
    public SLPlayer getChallengingPlayer() {
        return challenger;
    }
    
    private boolean isActive() {
        return active && secondsLeft > 0;
    }
    
    public void sendMessages(String prefix, String arena, Collection<Player> target) {
        BaseComponent[] intro = new ComponentBuilder(prefix).append(" ").append(challenger.getName() + " has challenged you to play on ").color(ChatColor.GREEN.asBungee()).append(arena + "!").color(ChatColor.RED.asBungee()).create();
        BaseComponent[] accept = 
                new ComponentBuilder(prefix)
                        .append(" [").color(ChatColor.GRAY.asBungee()).append("Accept").color(ChatColor.DARK_GREEN.asBungee()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/challenge accept " + challenger.getName())).append("]").color(ChatColor.GRAY.asBungee())
                        .append(" - ")
                        .append("[").color(ChatColor.GRAY.asBungee()).append("Decline").color(ChatColor.RED.asBungee()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/challenge decline " + challenger.getName())).append("]").color(ChatColor.GRAY.asBungee())
                        .create();
        for(Player player : target) {
            player.spigot().sendMessage(intro);
            player.spigot().sendMessage(accept);
        }
    }
    
    private static List<Challenge> challenges;
    
    public static void init() {
        challenges = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
            ArrayList<Challenge> toRemove = new ArrayList<>();
            for(Challenge c : challenges) {
                c.secondsLeft--;
                if(!c.isActive()) {
                    toRemove.add(c);
                    for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                        if(slp != c.getChallengingPlayer()) {
                            slp.removeChallenge(c);
                        }
                    }
                }
            }
            challenges.removeAll(toRemove);
        }, 0, 20);
    }
}
