/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.command.commands;

import java.util.Arrays;
import java.util.UUID;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.io.EntityBuilder;
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
public class kick extends BasicCommand{
    public kick(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        kick(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        kick(cs, cmd, args);
    }
    private void kick(CommandSender cs, Command cmd, String[] args) {
        if(args.length >= 2) {
            Player pl;
            if((pl = Bukkit.getPlayerExact(args[0])) != null) {
                String kickMessage = Arrays.toString(args).replaceFirst(args[0], "");
                pl.kickPlayer("You have been warned: " + kickMessage);
                Infraction kick = new Infraction(pl.getUniqueId(), cs instanceof Player ? ((Player)cs).getUniqueId() : UUID.fromString("00000000-0000-0000-0000-000000000000"), InfractionType.KICK, System.currentTimeMillis(), -1, kickMessage);
                EntityBuilder.save(kick, SpleefLeague.getInstance().getPluginDB().getCollection("Infractions"));
                success(cs, "The player has been kicked!");
            }
            else {
                error(cs, "The player \"" + args[0] + "\" is not online!");
            }
        }
        else {
            sendUsage(cs);
        }
    }
}
