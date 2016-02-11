package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.ChatChannel;
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
 * @author Patrick F.
 */

public class ticket extends BasicCommand {

    public ticket(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEFAULT);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args){
        if (args.length > 0) {
            String message = StringUtil.fromArgsArray(args);
            slp.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Ticket" + ChatColor.GRAY + "] " + ChatColor.GRAY + p.getName() + ": " + ChatColor.YELLOW + message);
            ChatManager.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Ticket" + ChatColor.GRAY + "]", ChatColor.GRAY + p.getName() + ": " + ChatColor.YELLOW + message, ChatChannel.STAFF);
        } else {
            sendUsage(p);
        }
    }
}
