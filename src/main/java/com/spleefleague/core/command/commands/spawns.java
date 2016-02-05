package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.spawn.SpawnManager;
import com.spleefleague.core.utils.ModifiableFinal;
import com.spleefleague.core.utils.TimeUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Created by Josh on 05/02/2016.
 */
public class spawns extends BasicCommand {

    public spawns(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        p.sendMessage(ChatColor.DARK_GRAY + "[ " + ChatColor.GRAY + "============ " + ChatColor.DARK_AQUA + "Spawns" + ChatColor.GRAY + " ============" + ChatColor.DARK_GRAY + " ]");

        ModifiableFinal<Integer> current = new ModifiableFinal<>(1);
        SpleefLeague.getInstance().getSpawnManager().getAll().forEach((SpawnManager.SpawnLocation spawnLocation) -> {
            ComponentBuilder componentBuilder = new ComponentBuilder("#" + current.getValue()).color(ChatColor.RED.asBungee())
                    .append(" | ").color(ChatColor.DARK_GRAY.asBungee())
                    .append("CLICK TO TELEPORT").color(ChatColor.GRAY.asBungee()).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tppos " + spawnLocation.getLocation().getBlockX() + " " + spawnLocation.getLocation().getBlockY() + ' ' + spawnLocation.getLocation().getBlockZ()))
                    .append(" | ").color(ChatColor.DARK_GRAY.asBungee())
                    .append(spawnLocation.getPlayersInRadius() + " players").color(ChatColor.GRAY.asBungee());
            slp.spigot().sendMessage(componentBuilder.create());
            current.setValue(current.getValue() + 1);
        });

        p.sendMessage(ChatColor.RED + "All spawns were cached " + TimeUtil.dateToString(new Date(SpleefLeague.getInstance().getSpawnManager().getLastCached()), false) + " ago.");
    }

}
