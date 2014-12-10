/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.command.commands;

import com.mongodb.BasicDBObject;
import java.util.UUID;
import net.spleefleague.core.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.utils.DatabaseConnection;
import net.spleefleague.core.utils.EntityBuilder;
import net.spleefleague.core.utils.StringUtil;
import net.spleefleague.core.infraction.Infraction;
import net.spleefleague.core.infraction.InfractionType;
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
        runConsole(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length >= 2){
            UUID id;
            if((id = DatabaseConnection.getUUID(args[0])) == null){
                error(cs, "The player \"" + args[0] + "\" has not been on the server yet!");
                return;
            }
            Player pl;
            String banMessage = StringUtil.fromArgsArray(args, 1);
            if((pl = Bukkit.getPlayerExact(args[0])) != null)
                pl.kickPlayer("You have been banned for: " + banMessage);
            Infraction ban = new Infraction(id, InfractionType.BAN, System.currentTimeMillis(), -1, banMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").remove(new BasicDBObject("uuid", id.toString()));
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"));
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions"));
            success(cs, "The player has been banned!");
        }
        else{
            sendUsage(cs);
        }
    }
}

