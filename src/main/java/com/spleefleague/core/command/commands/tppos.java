/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Jonas
 */
public class tppos extends BasicCommand{
    
    public tppos(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length == 3) {
            try {
                Location loc = new Location(p.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                p.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
            } catch(Exception e) {
                sendUsage(p);
            }
        }
        else if(args.length == 4) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                try {
                    Location loc = new Location(target.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    target.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
                } catch(Exception e) {
                    sendUsage(p);
                }
            }
            else {
                error(p, args[0] + " is not online!");
            }
        }
        else {
            sendUsage(p);
        }
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        runBlock(cs, cmd, args);
    }
    
    @Override
    protected int runBlock(CommandSender cs, Command cmd, String[] args) {
        if(args.length == 4) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                try {
                    Location loc = new Location(target.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    target.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
                } catch(Exception e) {
                    sendUsage(cs);
                }
            }
            else {
                error(cs, args[0] + " is not online!");
            }
        }
        else {
            sendUsage(cs);
        }
        return 0;
    }
}
