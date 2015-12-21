/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.listeners;

import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.packetwrapper.WrapperPlayServerEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.spleefleague.core.SpleefLeague;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Jonas
 */
public class VisibilityHandler implements Listener {

    private PacketAdapter chunk, chunkBulk, breakController, visibilityController;
    
    private VisibilityHandler() {
        initPacketListeners();
    }

    private void initPacketListeners() {
        visibilityController = new PacketAdapter(SpleefLeague.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerEntity wrapper = new WrapperPlayServerEntity(event.getPacket());
                if(hidden.get(event.getPlayer().getUniqueId()).contains(wrapper.getEntityID())) {
                    event.setCancelled(true);
                }
            }
        };
        manager.addPacketListener(visibilityController);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        hidden.put(event.getPlayer().getUniqueId(), new HashSet<>());
    }
    
    @EventHandler
    public void onQuit(PlayerJoinEvent event) {
        hidden.remove(event.getPlayer().getUniqueId());
    }
    
    private static final Map<UUID, Set<Integer>> hidden = new HashMap<>();

    private static final ProtocolManager manager;
    private static VisibilityHandler instance;
    
    public static void hide(Player toHide, Player... players) {
        destroyEntity(toHide, players);
        for(Player player : players) {
            hidden.get(player.getUniqueId()).add(toHide.getEntityId());
        }
    }
    
    public static void hide(Player toHide, Collection<Player> players) {
        destroyEntity(toHide, players);
        for(Player player : players) {
            hidden.get(player.getUniqueId()).add(toHide.getEntityId());
        }
    }
    
    public static void show(Player toHide, Player... players) {
        showEntity(toHide, players);
        for(Player player : players) {
            hidden.get(player.getUniqueId()).remove(toHide.getEntityId());
        }
    }
    
    public static void show(Player toHide, Collection<Player> players) {
        showEntity(toHide, players);
        for(Player player : players) {
            hidden.get(player.getUniqueId()).remove(toHide.getEntityId());
        }
    }
    
    public static boolean canSee(Player seeing, Player target) {
        return hidden.get(seeing.getUniqueId()).contains(target.getEntityId());
    }
    
    private static void destroyEntity(Entity toDestroy, Player... targets) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntityIds(new int[]{toDestroy.getEntityId()});
        for(Player player : targets) {
            wrapper.sendPacket(player);
        }
    }
    
    private static void destroyEntity(LivingEntity toDestroy, Collection<Player> targets) {
        WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
        wrapper.setEntityIds(new int[]{toDestroy.getEntityId()});
        for(Player player : targets) {
            wrapper.sendPacket(player);
        }
    }
    
    private static void showEntity(LivingEntity toShow, Player... targets) {
        WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving();
        wrapper.setEntityID(toShow.getEntityId());
        wrapper.setHeadPitch(toShow.getLocation().getPitch());
        wrapper.setHeadYaw(toShow.getLocation().getYaw());
        wrapper.setType(toShow.getType());
        wrapper.setX(toShow.getLocation().getX());
        wrapper.setY(toShow.getLocation().getY());
        wrapper.setZ(toShow.getLocation().getZ());
        for(Player player : targets) {
            wrapper.sendPacket(player);
        }
    }
    
    private static void showEntity(LivingEntity toShow, Collection<Player> targets) {
        WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving();
        wrapper.setEntityID(toShow.getEntityId());
        wrapper.setHeadPitch(toShow.getLocation().getPitch());
        wrapper.setHeadYaw(toShow.getLocation().getYaw());
        wrapper.setType(toShow.getType());
        wrapper.setX(toShow.getLocation().getX());
        wrapper.setY(toShow.getLocation().getY());
        wrapper.setZ(toShow.getLocation().getZ());
        for(Player player : targets) {
            wrapper.sendPacket(player);
        }
    }
    
    public static void stop() {
        if (instance != null) {
            manager.removePacketListener(instance.chunk);
            manager.removePacketListener(instance.chunkBulk);
            manager.removePacketListener(instance.breakController);
            manager.removePacketListener(instance.visibilityController);
            HandlerList.unregisterAll(instance);
            instance = null;
        }
    }

    public static void init() {
        if (instance == null) {
            instance = new VisibilityHandler();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    static {
        manager = ProtocolLibrary.getProtocolManager();
    }
}
