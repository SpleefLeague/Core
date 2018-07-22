/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.plugin.CorePlugin;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.v1_13_R1.ChatMessage;
import net.minecraft.server.v1_13_R1.Entity;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
/**
 *
 * @author Jonas
 */
public class PlayerUtil implements Listener{

    private static PlayerUtil instance;
    private final BukkitTask actionBarRunner;
    
    private PlayerUtil() {
        Bukkit.getPluginManager().registerEvents(this, SpleefLeague.getInstance());
        actionBarRunner = Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
            Iterator<Entry<Player, ActionBarData>> iter = activeActionBars.entrySet().iterator();
            while(iter.hasNext()) {
                Entry<Player, ActionBarData> entry = iter.next();
                ActionBarData abd = entry.getValue();
                if(abd.isDone()) {
                    if(!abd.fadeOut) {
                        rawTitle(entry.getKey(), EnumTitleAction.ACTIONBAR, new ChatMessage(""));
                    }
                    iter.remove();
                }
                else if(abd.shouldRefresh()) {
                    rawTitle(entry.getKey(), EnumTitleAction.ACTIONBAR, abd.message);
                }
                abd.tick();
            }
        }, 0, 1);
    }
    
    public static void init() {
        if(instance == null) {
            instance = new PlayerUtil();
        }
    }
    
    public static void title(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent titleJSON = ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
        if(title != null) rawTitle(player, EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        if(subtitle != null) rawTitle(player, EnumTitleAction.SUBTITLE, subtitleJSON);
    }
    
    public static void rawTitle(Player player, EnumTitleAction action, IChatBaseComponent chat, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftplayer = (CraftPlayer) player.getPlayer();
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(action, chat, fadeIn, stay, fadeOut);
        connection.sendPacket(titlePacket);
    }
    
    public static void rawTitle(Player player, EnumTitleAction action, IChatBaseComponent chat) {
        CraftPlayer craftplayer = (CraftPlayer) player.getPlayer();
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(action, chat);
        connection.sendPacket(titlePacket);
    }

    public static void clearPermissions(Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        try {
            Field field = CraftEntity.class.getDeclaredField("perm");
            field.setAccessible(true);
            Object o = field.get(cp);
            o.getClass().getMethod("clearPermissions").invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInWater(Player player) {
        Entity e = ((CraftPlayer) player).getHandle();
        return e.isInWater();
    }

    public static boolean isInLava(Player player) {
        Entity e = ((CraftPlayer) player).getHandle();
        //Find
        //return this.world.a(this.getBoundingBox().f(0.10000000149011612, 0.4000000059604645, 0.10000000149011612), Material.LAVA); in nms Entity
        return e.ax();
    }

    public static void sendToServer(Player player, String server) {
        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
            CorePlugin.syncSaveAll(player);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(SpleefLeague.getInstance(), "BungeeCord", out.toByteArray());
        });
    }
    
    public static void actionbar(Player player, IChatBaseComponent chat) {
        activeActionBars.remove(player);
        CraftPlayer craftplayer = (CraftPlayer) player.getPlayer();
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, chat);
        connection.sendPacket(titlePacket);
    }
    
    public static void actionbar(Player player, IChatBaseComponent chat, int duration, boolean fadeOut) {
        actionbar(player, chat);
        activeActionBars.put(player, new ActionBarData(chat, duration, fadeOut));
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activeActionBars.remove(event.getPlayer());
    }
    
    private static final Map<Player, ActionBarData> activeActionBars = new HashMap<>();
    
    private static class ActionBarData {
        private final IChatBaseComponent message;
        private final boolean fadeOut;
        private int durationLeft;
        private int nextRefresh;
        
        public ActionBarData(IChatBaseComponent message, int duration, boolean fadeOut) {
            this.message = message;
            this.nextRefresh = Math.min(20, duration);
            this.durationLeft = duration;
            this.fadeOut = fadeOut;
        }
        
        public boolean shouldRefresh() {
            return !isDone() && nextRefresh == 0;
        }
        
        public boolean isDone() {
            return durationLeft <= 0;
        }
        
        public void tick() {
            durationLeft--;
            nextRefresh--;
            if(nextRefresh < 0) {
                nextRefresh = Math.min(20, durationLeft);
            }
        }
    }
    
    /*
    20
    2
    */
}
