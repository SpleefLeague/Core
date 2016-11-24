package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.spleefleague.core.utils.UtilChat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class FakeNpcScoreboard {

    static void register(Player p) {
        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName("FAKENPCS");
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        wrapper.setPrefix(UtilChat.c("&7[NPC] "));
        List<String> players = FakeEntitiesManager.getCreatures().values().stream()
                .filter(c -> c instanceof FakeNpc).map(c -> (FakeNpc) c).map(npc -> npc.getName())
                .collect(Collectors.toList());
        wrapper.setPlayers(players);
        wrapper.sendPacket(p);
    }
    
    static void addNpc(FakeNpc npc) {
        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName("FAKENPCS");
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);
        wrapper.setPlayers(Collections.singletonList(npc.getName()));
        Bukkit.getOnlinePlayers().forEach(wrapper::sendPacket);
    }
    
    static void removeNpc(FakeNpc npc) {
        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName("FAKENPCS");
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_REMOVED);
        wrapper.setPlayers(Collections.singletonList(npc.getName()));
        Bukkit.getOnlinePlayers().forEach(wrapper::sendPacket);
    }
    
}
