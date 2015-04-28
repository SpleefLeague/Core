/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.plugin.GamePlugin;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class leave extends BasicCommand{

    public leave(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(GamePlugin.isQueuedAll(p)) {
            GamePlugin.dequeueAll(p);
            success(p, "You have successfully been removed from the queue.");
        }
        else {
            error(p, "You are currently not queued.");
        }
    }
}
