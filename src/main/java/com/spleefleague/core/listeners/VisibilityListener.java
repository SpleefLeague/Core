package com.spleefleague.core.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.events.BattleStartEvent;
import com.spleefleague.core.player.GeneralPlayer;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Josh Keighley
 */
public class VisibilityListener implements Listener {

    private static Listener instance;

    public static void init() {
        if (instance == null) {
            instance = new VisibilityListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }

    private VisibilityListener() {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStart(BattleStartEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(SpleefLeague.getInstance(), () -> {
            //Handle people in battle seeing others.
            List<PlayerInfoData> list = new ArrayList<>();
            SpleefLeague.getInstance().getPlayerManager().getAll().forEach((SLPlayer slPlayer) -> list.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(slPlayer.getPlayer()), ((CraftPlayer) slPlayer.getPlayer()).getHandle().ping, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(slPlayer.getRank().getColor() + slPlayer.getName()))));
            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
            packet.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            packet.setData(list);
            e.getBattle().getActivePlayers().forEach((Object ratedPlayer) -> packet.sendPacket(((GeneralPlayer) ratedPlayer).getPlayer()));

            //Handle others seeing people in battle.
            list.clear();
            e.getBattle().getActivePlayers().forEach((Object ratedPlayer) -> {
                SLPlayer generalPlayer = SpleefLeague.getInstance().getPlayerManager().get(((GeneralPlayer) ratedPlayer).getPlayer());
                list.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(generalPlayer.getPlayer()), ((CraftPlayer) generalPlayer.getPlayer()).getHandle().ping, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(generalPlayer.getRank().getColor() + generalPlayer.getName())));
            });
            packet.setData(list);
            SpleefLeague.getInstance().getPlayerManager().getAll().forEach((SLPlayer slPlayer) -> packet.sendPacket(slPlayer.getPlayer()));
        }, 10);
    }

}
