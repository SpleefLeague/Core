/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import java.util.Date;
import java.util.UUID;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.infraction.Infraction;
import net.spleefleague.core.io.EntityBuilder;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.utils.DatabaseConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class infractions extends BasicCommand {

    public infractions(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        infractions(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        infractions(cs, cmd, args);
    }
    
    private void infractions(CommandSender cs, Command cmd, String[] args) {
        if(args.length >= 1) {
            UUID id;
            if((id = DatabaseConnection.getUUID(args[0])) == null) {
                error(cs, "The player \"" + args[0] + "\" has not been on the server yet!");
                return;
            }
            DBCursor dbc = SpleefLeague.getInstance().getPluginDB().getCollection("Infractions").find(new BasicDBObject("uuid", id.toString()));
            if(dbc.count() == 0) {
                error(cs, "The player \"" + args[0] + "\" doesn't have any infractions yet!");
                return;
            }
            int page = 1;
            if(args.length != 1) {
                int argsPage;
                try {
                    argsPage = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException e) {
                    error(cs, "\"" + args[1] + "\" is not a number!");
                    return;
                }
                int maxPages = (dbc.count() - 1) / 10 + 1;
                if(argsPage > 0 &&  maxPages >= argsPage) {
                    page = argsPage;
                }
                else {
                    error(cs, argsPage + " is not a valid page." + (maxPages == 1 ? " There is only one page!" : " Please choose a number between 1 and " + maxPages + "!"));
                    return;
                }
            }
            cs.sendMessage("[==========(" + (page * 10 - 9) + "-" + (page * 10 <= dbc.count() ? page * 10 : dbc.count()) + ")==========]");
            dbc.skip((page - 1) * 10);
            for(int i = (page - 1) * 10; i < (page * 10 <= dbc.count() ? page * 10 : dbc.count()); i++) {
                Infraction inf = EntityBuilder.load(dbc.next(), Infraction.class);
                cs.sendMessage((i + 1) + ". | " + inf.getType().getColor() + inf.getType() + ChatColor.WHITE + " | " + (inf.getPunisher().equals(UUID.fromString("00000000-0000-0000-0000-000000000000")) ? "CONSOLE" : DatabaseConnection.getUsername(inf.getPunisher())) + " | " + new Date(inf.getTime()) + " | " + inf.getMessage());
            }
        }
        else {
            sendUsage(cs);
        }
    }
}
