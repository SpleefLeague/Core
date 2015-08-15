/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.plugin.GamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class spectate extends BasicCommand {

    public spectate(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length == 0) {
            if (GamePlugin.isSpectatingGlobal(p)) {
                GamePlugin.unspectateGlobal(p);
                success(p, "You are no longer spectating a match");
                p.setAllowFlight(false);
                p.setFlying(false);
            }
            else {
                error(p, "You are currently not spectating anyone!");
            }
        }
        else if (args.length == 1) {
            if (!GamePlugin.isIngameGlobal(p)) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    for (GamePlugin gp : GamePlugin.getGamePlugins()) {
                        if (gp.isIngame(target)) {
                            if (GamePlugin.isSpectatingGlobal(p)) {
                                GamePlugin.unspectateGlobal(p);
                            }
                            if (gp.spectate(target, p)) {
                                success(p, "You are now spectating " + target.getName());
                            }
                        }
                    }
                }
                else {
                    error(p, args[0] + " is not online!");
                }
            }
            else {
                error(p, "You are currently ingame!");
            }
        }
        else {
            sendUsage(p);
        }
    }
}
