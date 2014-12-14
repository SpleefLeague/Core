/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import net.spleefleague.core.chat.Theme;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class surrender extends BasicCommand{

    public surrender(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        p.sendMessage(Theme.INFO + "/surrender is currently not available. If you really want to surrender, simply leave and join the server again.");
    }
}