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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class unban extends BasicCommand{
    public unban(CorePlugin plugin, String name, String usage) {
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
            String unbanMessage = StringUtil.fromArgsArray(args, 1);
            Infraction unban = new Infraction(id, InfractionType.UNBAN, System.currentTimeMillis(), -1, unbanMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").remove(new BasicDBObject("uuid", id.toString()));
            EntityBuilder.save(unban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"), null);
            success(cs, "The player has been unbanned!");
        }
        else{
            sendUsage(cs);
        }
    }
    
}
