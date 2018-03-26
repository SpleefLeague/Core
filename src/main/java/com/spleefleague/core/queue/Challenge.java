/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.queue;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.SLPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jonas
 */
public abstract class Challenge {

    private final SLPlayer challenger;
    private boolean active = true;
    private int secondsLeft = 60;
    private final boolean[] accepted;
    private final SLPlayer[] players;

    public Challenge(SLPlayer challenger, SLPlayer... challenged) {
        this.challenger = challenger;
        this.accepted = new boolean[challenged.length + 1];
        this.players = new SLPlayer[challenged.length + 1];
        for (int i = 0; i < challenged.length; i++) {
            this.players[i+1] = challenged[i];
        }
        this.players[0] = challenger;
        this.accepted[0] = true;
        challenges.add(this);
    }

    public void accept(SLPlayer player) {
        if (!this.hasAccepted(player)) {
            int index = this.getPlayerIndex(player);
            if (index == -1) {
                player.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + ChatColor.RED + "Your challenge is invalid");
                player.removeChallenge(this);
                return;
            }
            this.accepted[index] = true;
            challenger.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + ChatColor.RED + player.getName() + ChatColor.GREEN + " has accepted your challenge.");
            if (this.hasEveryoneAccepted()) {
                active = false;
                start(players);
            }
        }
    }

    private int getPlayerIndex(SLPlayer p) {
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i].getUniqueId() == p.getUniqueId()) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasEveryoneAccepted() {
        for (int i = 0; i < this.accepted.length; i++) {
            if (!this.accepted[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAccepted(SLPlayer player) {
        int index = getPlayerIndex(player);
        if (index == -1) {
            return false;
        }
        return this.accepted[index];
    }

    public void decline(SLPlayer player) {
        active = false;
        for (SLPlayer slp : players) {
            if(slp != player) {
                slp.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + ChatColor.RED + player.getName() + " has declined the challenge.");
            }
        }
    }

    public abstract void start(SLPlayer[] accepted);

    public SLPlayer getChallengingPlayer() {
        return challenger;
    }

    private boolean isActive() {
        return active && secondsLeft > 0;
    }

    public void sendMessages(String prefix, String arena, Collection<? extends Player> target) {
        BaseComponent[] intro;
        if(arena != null) {
            intro = new ComponentBuilder(prefix).append(" ").append(challenger.getName() + " has challenged you to play on ").color(ChatColor.GREEN.asBungee()).append(arena + "!").color(ChatColor.RED.asBungee()).create();
        }
        else {
            intro = new ComponentBuilder(prefix).append(" ").append(challenger.getName() + " has challenged you to play").append(arena + "!").color(ChatColor.RED.asBungee()).create();
        }
        BaseComponent[] accept
                = new ComponentBuilder(prefix)
                .append(" [").color(ChatColor.GRAY.asBungee()).append("Accept").color(ChatColor.DARK_GREEN.asBungee()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/challenge accept " + challenger.getName())).append("]").color(ChatColor.GRAY.asBungee())
                .append(" - ")
                .append("[").color(ChatColor.GRAY.asBungee()).append("Decline").color(ChatColor.RED.asBungee()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/challenge decline " + challenger.getName())).append("]").color(ChatColor.GRAY.asBungee())
                .create();
        for (Player player : target) {
            player.spigot().sendMessage(intro);
            player.spigot().sendMessage(accept);
        }
    }

    private static List<Challenge> challenges;

    public static void init() {
        challenges = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
            ArrayList<Challenge> toRemove = new ArrayList<>();
            for (Challenge c : challenges) {
                c.secondsLeft--;
                if (!c.isActive()) {
                    toRemove.add(c);
                    for (SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
                        if (slp != c.getChallengingPlayer()) {
                            slp.removeChallenge(c);
                        }
                    }
                }
            }
            challenges.removeAll(toRemove);
        }, 0, 20);
    }
}
