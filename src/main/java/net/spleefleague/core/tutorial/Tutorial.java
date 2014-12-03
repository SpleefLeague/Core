/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.tutorial;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.ChatChannelMessageEvent;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.part.Introduction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Jonas
 */
public class Tutorial {
    
    public final SLPlayer slp;
    private LinkedList<TutorialPart> parts;
    //Previous settings
    private HashSet<String> receivingChannels;
    private String sendingChannel;
    
    public Tutorial(SLPlayer slplayer) {
        this.slp = slplayer;
        initialize();
    }
    
    public void start() {
        this.receivingChannels = (HashSet<String>)slp.getReceivingChatChannels().clone();
        this.sendingChannel = slp.getSendingChannel();
        this.slp.setSendingChannel("TUTORIAL-" + slp.getUUID().toString());
        this.slp.getReceivingChatChannels().clear();
        this.slp.addChatChannel(this.slp.getSendingChannel());
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player != slp.getPlayer()) {
                player.hidePlayer(slp.getPlayer());
                slp.getPlayer().hidePlayer(player);
            }
        }
        tutorials.add(this);
        parts.peek().start();
    }
    
    public void end() {
        slp.setSendingChannel(sendingChannel);
        slp.setReceivingChatChannels(receivingChannels);
        tutorials.remove(this);
        for(SLPlayer p : SpleefLeague.getInstance().getPlayerManager().getAll()) {
            if(!isInTutorial(p)) {
                p.getPlayer().showPlayer(slp.getPlayer());
                slp.getPlayer().showPlayer(p.getPlayer());
            }
        }
    }
    
    private void initialize() {
        this.parts = new LinkedList<>();
        parts.push(new Introduction(slp, random.nextInt(100000)));
    }
    
    protected void next() {
        parts.poll().onComplete();
        parts.peek().start();
    }
    
    public SLPlayer getPlayer() {
        return slp;
    }
    
    private static final HashSet<Tutorial> tutorials = new HashSet<>();
    protected static final Random random = new Random();
    
    private boolean isInTutorial(SLPlayer slp) {
        return getTutorial(slp) != null;
    }
    
    private Tutorial getTutorial(SLPlayer slp) {
        for(Tutorial t : tutorials) {
            if(slp == t.slp) {
                return t;
            }
        }
        return null;
    }
    
    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player p = event.getPlayer();
                for(Tutorial t : tutorials) {
                    p.hidePlayer(t.getPlayer().getPlayer());
                    t.getPlayer().getPlayer().hidePlayer(p);
                }
            }
            
            @EventHandler
            public void onMessage(ChatChannelMessageEvent event) {
                for(Tutorial t : tutorials) {
                    if(event.getChannel().equals("TUTORIAL-" + t.getPlayer().getUUID().toString())) {
                        t.parts.peek().onPlayerMessage(event.getMessage());
                        break;
                    }
                }
            }
        }, SpleefLeague.getInstance());
    }
}