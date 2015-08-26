/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 *
 * @author Jonas
 */
public class tp extends BasicCommand{
    
    public tp(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR, Rank.BUILDER, Rank.ORGANIZER);
    }
    
    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                if(slp.getRank() == Rank.ORGANIZER) {
                    if(SpleefLeague.getInstance().getPlayerManager().get(target).getState() != PlayerState.INGAME) {
                        error(p, "You may only to teleport to ingame players!");
                        return;
                    }
                }
                p.teleport(target, TeleportCause.COMMAND);
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
