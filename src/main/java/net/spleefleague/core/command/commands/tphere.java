/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Jonas
 */
public class tphere extends BasicCommand{
    
    public tphere(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                target.teleport(p, PlayerTeleportEvent.TeleportCause.COMMAND);
            }
            else {
                error(p, args[0] + " is not online!");
            }
        }
        else {
            sendUsage(p);
        }
    }
}
