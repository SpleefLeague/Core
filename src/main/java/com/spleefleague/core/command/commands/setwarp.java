package com.spleefleague.core.command.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.Warp;

public class setwarp extends BasicCommand {

    public setwarp(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR, Rank.BUILDER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length == 1) {
            Warp warp = new Warp(args[0], p.getLocation());
            Warp.addWarp(warp);
            success(p, "Warp '" + warp.getName() + "' saved");
        } else {
            sendUsage(p);
        }
    }

}
