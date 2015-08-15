/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class staffchat extends BasicCommand {

    public staffchat(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length > 0) {
            String message = StringUtil.fromArgsArray(args, 0);
            ChatManager.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Staff" + ChatColor.GRAY + "]", ChatColor.GRAY + p.getName() + ": " + ChatColor.GREEN + message, "STAFF");
        }
        else {
            sendUsage(p);
        }
    }
}
