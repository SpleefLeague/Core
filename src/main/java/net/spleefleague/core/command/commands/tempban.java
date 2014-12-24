/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.command.commands;

import com.mongodb.BasicDBObject;
import java.time.Duration;
import java.util.UUID;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.utils.DatabaseConnection;
import net.spleefleague.core.io.EntityBuilder;
import net.spleefleague.core.utils.StringUtil;
import net.spleefleague.core.utils.TimeUtil;
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
public class tempban extends BasicCommand{
    public tempban(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length >= 3){
            UUID id;
            if((id = DatabaseConnection.getUUID(args[0])) == null){
                error(cs, "The player \"" + args[0] + "\" has not been on the server yet!");
                return;
            }
            Player pl;
            Duration duration = TimeUtil.parseDurationString(args[1]);
            String tempbanMessage = StringUtil.fromArgsArray(args, 2);
            if((pl = Bukkit.getPlayerExact(args[0])) != null)
                pl.kickPlayer("You have been tempbanned for " + TimeUtil.getFormatted(duration) + ". " + tempbanMessage);
            Infraction tempban = new Infraction(id, InfractionType.TEMPBAN, System.currentTimeMillis(), duration.toMillis(), tempbanMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").remove(new BasicDBObject("uuid", id.toString()));
            EntityBuilder.save(tempban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"));
            EntityBuilder.save(tempban, SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions"));
            success(cs, "The player has been tempbanned!");
        }
        else{
            sendUsage(cs);
        }
    }
}
