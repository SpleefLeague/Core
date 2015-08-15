/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;


import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class PlayerUtil {
    
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }
    
    public static void clearPermissions(Player player) {
        CraftPlayer cp = (CraftPlayer)player;
        try {
            Field field = CraftEntity.class.getDeclaredField("perm");
            field.setAccessible(true);
            Object o = field.get(cp);
            o.getClass().getMethod("clearPermissions").invoke(o);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isInWater(Player player) {
        Entity e = ((CraftPlayer)player).getHandle();
        return e.V();
    }
    
    public static boolean isInLava(Player player) {
        Entity e = ((CraftPlayer)player).getHandle();
        return e.ab();
    }
}