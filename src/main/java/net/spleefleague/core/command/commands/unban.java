/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.command.commands;

import com.mongodb.BasicDBObject;
import java.util.Arrays;
import java.util.UUID;
import net.spleefleague.core.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.utils.DatabaseConnection;
import net.spleefleague.core.utils.EntityBuilder;
import net.spleefleague.infraction.Infraction;
import net.spleefleague.infraction.InfractionType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
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
        if(args.length >= 2){
            UUID id;
            if((id = DatabaseConnection.getUUID(args[0])) == null){
                error(p, "The player \"" + "\" has not been on the server yet!");
                return;
            }
            String unbanMessage = Arrays.toString(args).replaceFirst(args[0], "");
            Infraction ban = new Infraction(id, InfractionType.UNBAN, System.currentTimeMillis(), -1, unbanMessage);
            SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").remove(new BasicDBObject("uuid", id.toString()));
            EntityBuilder.save(ban, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"), new BasicDBObject("uuid", id.toString()), true);
            success(p, "The player has been banned!");
        }
        else{
            sendUsage(p);
        }
    }
    
}
