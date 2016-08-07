package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.*;

public class sping extends BasicCommand {
    public sping(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEFAULT);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        ArrayList<String> names = new ArrayList<>();
        for (String s : args) {
            names.add(s);
        }
        Map<String, Integer> pings = new HashMap();
        for (EntityPlayer ep : DedicatedServer.getServer().getPlayerList().players) {
            if (!names.isEmpty()) {
                if (!names.contains(ep.getName())) {
                    continue;
                }
            }
            SLPlayer slpl = SpleefLeague.getInstance().getPlayerManager().get(ep.getUniqueID());
            pings.put(slpl.getRank().getColor() + slpl.getName(), ep.ping);
        }
        pings = sortByValue(pings);
        p.sendMessage(ChatColor.DARK_AQUA + "[====== " + ChatColor.GOLD + "Everyone's Pings" + ChatColor.DARK_AQUA + " ======]");
        for (Map.Entry<String, Integer> pv : pings.entrySet()) {
            p.sendMessage(getPingColor(pv.getValue()) + Integer.toString(pv.getValue()) + ChatColor.GRAY + " >> " + pv.getKey());
        }
    }

    public ChatColor getPingColor(int ping) {
        ChatColor c = ChatColor.DARK_RED;
        if (ping < 30) {
            c = ChatColor.DARK_GREEN;
        } else if (ping < 60) {
            c = ChatColor.GREEN;
        } else if (ping < 120) {
            c = ChatColor.YELLOW;
        } else if (ping < 250) {
            c = ChatColor.GOLD;
        } else if (ping < 500) {
            c = ChatColor.RED;
        } else {
            c = ChatColor.DARK_RED;
        }
        return c;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
