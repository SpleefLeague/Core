/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.utils.StringUtil;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.infraction.InfractionType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class warn extends BasicCommand {

    public warn(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        warn(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        warn(cs, cmd, args);
    }

    private void warn(CommandSender cs, Command cmd, String[] args) {
        if (args.length >= 2) {
            Player pl;
            if ((pl = Bukkit.getPlayerExact(args[0])) != null) {
                String warnMessage = StringUtil.fromArgsArray(args, 1);
                pl.sendMessage(Theme.ERROR + "You have been warned: " + ChatColor.GRAY + warnMessage);
                Infraction warn = new Infraction(pl.getUniqueId(), cs instanceof Player ? ((Player) cs).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), InfractionType.WARNING, System.currentTimeMillis(), -1, warnMessage);
                Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> EntityBuilder.save(warn, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions")));
                ChatManager.sendMessage(new ComponentBuilder(SpleefLeague.getInstance().getChatPrefix() + " ").append(pl.getName() + " has been warned by " + cs.getName() + "!").color(ChatColor.GRAY)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + warnMessage).color(ChatColor.GRAY).create())).create(), ChatChannel.STAFF_NOTIFICATIONS);
                success(cs, "The player has been warned!");
            } else {
                error(cs, "The player \"" + args[0] + "\" is not online!");
            }
        } else {
            sendUsage(cs);
        }
    }
}
