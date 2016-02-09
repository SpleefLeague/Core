package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Patrick F.
 */

public class ticketreply extends BasicCommand {

    public ticketreply(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length > 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player.isOnline()) {
                String message = StringUtil.fromArgsArray(args);
                ChatManager.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Ticket|" + player.getName() + ChatColor.GRAY + "]", ChatColor.GRAY + p.getName() + ": " + ChatColor.YELLOW + message, ChatChannel.STAFF);
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "Ticket|" + player.getName() + ChatColor.GRAY + "]" + ChatColor.GRAY + p.getName() + ": " + ChatColor.YELLOW + message);
            }
            else {
                error(p, player.getName() + " is not currently online.");
            }
            
        }
        else {
            sendUsage(p);
        }
    }
}
