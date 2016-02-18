package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
            slp.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Ticket" + ChatColor.DARK_GREEN + "|" + ChatColor.GREEN + slp.getName() + ChatColor.DARK_GREEN + "] " + slp.getRank().getColor() + slp.getName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + message);
            ChatManager.sendMessage(ChatChannel.STAFF, new ComponentBuilder("[").color(ChatColor.DARK_GREEN.asBungee()).append("Ticket").color(ChatColor.GREEN.asBungee()).append("|").color(ChatColor.DARK_GREEN.asBungee()).append(slp.getName()).color(ChatColor.GREEN.asBungee()).append("] ").color(ChatColor.DARK_GREEN.asBungee())
                    .append(slp.getName()).color(slp.getRank().getColor().asBungee()).append(": ").color(ChatColor.GRAY.asBungee()).append(message).color(ChatColor.YELLOW.asBungee())
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to respond!").color(ChatColor.GRAY.asBungee()).create()))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/treply " + slp.getName() + " ")).create());
        }
        else {
            sendUsage(p);
        }
    }
}
