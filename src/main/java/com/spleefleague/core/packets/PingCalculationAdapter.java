/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.spleefleague.virtualworld.VirtualWorld;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutKeepAlive;
import net.minecraft.server.v1_12_R1.PacketPlayInKeepAlive;

/**
 *
 * @author balsfull
 */
public class PingCalculationAdapter extends PacketAdapter {
    
    public PingCalculationAdapter(int refreshRate) {
        super(VirtualWorld.getInstance(), ListenerPriority.LOW, new PacketType[]{ 
            Client.KEEP_ALIVE,
            Server.KEEP_ALIVE
        });
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                try {
                    PacketPlayOutKeepAlive ppoka = PacketPlayOutKeepAlive.class.getConstructor(long.class).newInstance(-System.currentTimeMillis());
                    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(ppoka);
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(ArtificialThrottleAdapter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, refreshRate, refreshRate);
    }
    
    @Override
    public void onPacketSending(PacketEvent event) {
        
    }
    
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if(event.isCancelled()) return;
        try {
            PacketPlayInKeepAlive ppika = (PacketPlayInKeepAlive)event.getPacket().getHandle();
            Field f = PacketPlayInKeepAlive.class.getDeclaredField("a");
            f.setAccessible(true);
            long timestamp = f.getLong(ppika);
            if(timestamp < 0) {
                long ping = timestamp - -System.currentTimeMillis();
                ((CraftPlayer)event.getPlayer()).getHandle().ping = (int)ping;
                event.setCancelled(true);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ArtificialThrottleAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
