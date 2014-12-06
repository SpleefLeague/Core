/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.tutorial;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.events.ChatChannelMessageEvent;
import net.spleefleague.core.events.GeneralPlayerLoadedEvent;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.part.Introduction;
import net.spleefleague.core.utils.ControllableVillager;
import net.spleefleague.core.utils.packetwrapper.WrapperPlayServerEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Jonas
 */
public class Tutorial {
    
    public final SLPlayer slp;
    private LinkedList<TutorialPart> parts;
    private Location startLocation;
    //Previous settings
    private HashSet<String> receivingChannels;
    private String sendingChannel;
    private Entity entity;
    private PacketListener pl;
    
    public Tutorial(SLPlayer slplayer) {
        this.slp = slplayer;
        initTurorial();
    }
    
    
    public void start() {
        this.receivingChannels = (HashSet<String>)slp.getReceivingChatChannels().clone();
        this.sendingChannel = slp.getSendingChannel();
        this.slp.setSendingChannel("TUTORIAL-" + slp.getUUID().toString());
        this.slp.getReceivingChatChannels().clear();
        this.slp.addChatChannel(this.slp.getSendingChannel());
        this.startLocation = slp.getPlayer().getLocation();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player != slp.getPlayer()) {
                player.hidePlayer(slp.getPlayer());
                slp.getPlayer().hidePlayer(player);
            }
        }
        tutorials.add(this);
        pl.start();
        parts.peek().start();
        
    }
    
    public void end(boolean completed) {
        if(this.entity != null) {
            this.entity.remove();
        }
        slp.getPlayer().teleport(this.startLocation);
        slp.setSendingChannel(sendingChannel);
        slp.setReceivingChatChannels(receivingChannels);
        if(completed) slp.setCompletedTutorial(true);
        tutorials.remove(this);
        for(SLPlayer p : SpleefLeague.getInstance().getPlayerManager().getAll()) {
            if(!isInTutorial(p)) {
                p.getPlayer().showPlayer(slp.getPlayer());
                slp.getPlayer().showPlayer(p.getPlayer());
            }
        }
        pl.stop();
    }
    
    private void initTurorial() {
        this.parts = new LinkedList<>();
        parts.push(new Introduction(slp, this));
        pl = new PacketListener(SpleefLeague.getInstance().getProtocolManager(), this);
    }
    
    protected void next() {
        parts.poll().onComplete();
        if(parts.isEmpty()) {
            end(true);
        }
        else {
            parts.peek().start();
        }
    }
    
    public SLPlayer getPlayer() {
        return slp;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    private static final HashSet<Tutorial> tutorials = new HashSet<>();
    protected static final Random random = new Random();
    
    public static boolean isInTutorial(SLPlayer slp) {
        return getTutorial(slp) != null;
    }
    
    public static Tutorial getTutorial(SLPlayer slp) {
        for(Tutorial t : tutorials) {
            if(slp == t.slp) {
                return t;
            }
        }
        return null;
    }
    
    public static HashSet<Tutorial> getTutorials() {
        return tutorials;
    }
    
    public static void initialize() {}
    
    public static class TutorialData {
        public Entity entity;
    }
    
    private static class PacketListener {
        private final ProtocolManager manager;
        private final PacketAdapter pa;
        
        public PacketListener (final ProtocolManager manager, final Tutorial tutorial) {
            this.manager = manager;
            pa = new PacketAdapter(SpleefLeague.getInstance(), Server.ENTITY) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrapperPlayServerEntity packet = new WrapperPlayServerEntity(event.getPacket());
                    System.out.println(packet.getEntity(event) + ": " + (packet.getEntity(event) instanceof ControllableVillager));
                    if(packet.getEntityID() == tutorial.getEntity().getEntityId()) {
                        if(event.getPlayer() != tutorial.getPlayer().getPlayer()) {
                            event.setCancelled(true);
                        }
                    }
                }

                @Override
                public void onPacketReceiving(PacketEvent event) {

                }
            };
        }
        
        public void stop() {
            this.manager.removePacketListener(pa);
        }
        
        public void start() {
            this.manager.addPacketListener(pa);
        }
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
            @EventHandler
            public void onLoaded(GeneralPlayerLoadedEvent event) {
                if(event.getGeneralPlayer() instanceof SLPlayer) {
                    SLPlayer slp = (SLPlayer)event.getGeneralPlayer();
                    if(!slp.hasCompletedTutorial()) {
                        Tutorial tutorial = new Tutorial(slp);
                        tutorial.start();
                    }
                }
            }
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                for(Tutorial t : tutorials) {
                    if(t.getPlayer().getPlayer() == event.getPlayer()) {
                        t.end(false);
                        break;
                    }
                }
            }
            
            @EventHandler
            public void onEntityDamage(EntityDamageEvent event) {
                Entity entity = event.getEntity();
                for(Tutorial t : tutorials) {
                    if(t.getPlayer().getPlayer() == entity || t.getEntity() == entity) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }, SpleefLeague.getInstance());
    }
}