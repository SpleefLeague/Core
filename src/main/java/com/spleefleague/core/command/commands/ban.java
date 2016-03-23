/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import java.util.UUID;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.DatabaseConnection;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.utils.StringUtil;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.infraction.InfractionType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class ban extends BasicCommand {

    public ban(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        ban(p, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        ban(cs, args);
    }

    private void ban(CommandSender cs, String[] args) {
        if (args.length >= 2) {
            UUID id;
            if ((id = DatabaseConnection.getUUID(args[0])) == null) {
                error(cs, "The player \"" + args[0] + "\" has not been on the server yet!");
                return;
            }
            Player pl;
            String banMessage = StringUtil.fromArgsArray(args, 1);
            Infraction ban = new Infraction(id, cs instanceof Player ? ((Player) cs).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), InfractionType.BAN, System.currentTimeMillis(), -1, banMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").deleteOne(new Document("uuid", id.toString()));
            Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"), false);
                EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions"), false);
            });
            ChatManager.sendMessage(new ComponentBuilder(SpleefLeague.getInstance().getChatPrefix() + " ").append(args[0] + " has been banned by " + cs.getName() + "!").color(ChatColor.GRAY)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Reason: " + banMessage).color(ChatColor.GRAY).create())).create(), ChatChannel.STAFF_NOTIFICATIONS);
            if ((pl = Bukkit.getPlayerExact(args[0])) != null) {
                pl.kickPlayer("You have been banned for: " + banMessage);
            }
            success(cs, "The player has been banned!");
        } else {
            sendUsage(cs);
        }
    }
}
