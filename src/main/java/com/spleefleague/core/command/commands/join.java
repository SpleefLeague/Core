/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class join extends BasicCommand {

    public join(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(SpleefLeague.getInstance(), "BungeeCord");
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length == 1) {
            PlayerUtil.sendToServer(p, args[0]);
        } else {
            sendUsage(p);
        }
    }
}
