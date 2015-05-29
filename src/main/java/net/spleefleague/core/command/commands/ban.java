/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.command.commands;

import java.util.UUID;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.utils.DatabaseConnection;
import net.spleefleague.core.io.EntityBuilder;
import net.spleefleague.core.utils.StringUtil;
import net.spleefleague.core.infraction.Infraction;
import net.spleefleague.core.infraction.InfractionType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class ban extends BasicCommand{
    public ban(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        ban(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        ban(cs, cmd, args);
    }
    private void ban(CommandSender cs, Command cmd, String[] args) {
        if(args.length >= 2) {
            UUID id;
            if((id = DatabaseConnection.getUUID(args[0])) == null) {
                error(cs, "The player \"" + args[0] + "\" has not been on the server yet!");
                return;
            }
            Player pl;
            String banMessage = StringUtil.fromArgsArray(args, 1);
            Infraction ban = new Infraction(id, cs instanceof Player ? ((Player)cs).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), InfractionType.BAN, System.currentTimeMillis(), -1, banMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").deleteOne(new Document("uuid", id.toString()));
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"), false);
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions"), false);
            if((pl = Bukkit.getPlayerExact(args[0])) != null)
                pl.kickPlayer("You have been banned for: " + banMessage);
            success(cs, "The player has been banned!");
        }
        else {
            sendUsage(cs);
        }
    }
}

