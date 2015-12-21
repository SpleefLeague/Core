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
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class unban extends BasicCommand{
    public unban(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.SENIOR_MODERATOR);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        unban(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        unban(cs, cmd, args);
    }
    private void unban(CommandSender cs, Command cmd, String[] args) {
        if(args.length >= 2) {
            UUID id;
            if((id = DatabaseConnection.getUUID(args[0])) == null) {
                error(cs, "The player \"" + args[0] + "\" has not been on the server yet!");
                return;
            }
            String unbanMessage = StringUtil.fromArgsArray(args, 1);
            Infraction unban = new Infraction(id, cs instanceof Player ? ((Player)cs).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), InfractionType.UNBAN, System.currentTimeMillis(), -1, unbanMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").deleteOne(new Document("uuid", id.toString()));
            EntityBuilder.save(unban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"));
            ChatManager.sendMessage(SpleefLeague.getInstance().getChatPrefix() + Theme.SUPER_SECRET.buildTheme(false) + " The player " + args[0] + " has been unbanned by " + cs.getName(), ChatChannel.STAFF_NOTIFICATIONS);
            success(cs, "The player has been unbanned!");
        }
        else {
            sendUsage(cs);
        }
    }
}
