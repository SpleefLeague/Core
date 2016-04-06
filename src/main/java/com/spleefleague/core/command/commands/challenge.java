/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.PlayerState;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.queue.Challenge;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class challenge extends BasicCommand {

    public challenge(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (slp.getState() != PlayerState.INGAME) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("accept")) {
                    SLPlayer target = SpleefLeague.getInstance().getPlayerManager().get(args[1]);
                    if (target.getState() == PlayerState.INGAME) {
                        error(p, "The player is already in a game");
                        return;
                    }
                    if (target != null) {
                        Challenge challenge = slp.getChallenge(target);
                        if (challenge != null) {
                            challenge.accept(slp);
                            success(p, "You accepted the challenge!");
                        } else {
                            error(p, "You have no open challenges by " + target.getName() + "!");
                        }
                    } else {
                        error(p, "The player " + args[0] + " is not online!");
                    }
                } else if (args[0].equalsIgnoreCase("decline")) {
                    SLPlayer target = SpleefLeague.getInstance().getPlayerManager().get(args[1]);
                    if (target != null) {
                        Challenge challenge = slp.getChallenge(target);
                        if (challenge != null) {
                            challenge.decline(slp);
                            error(p, "You have declined the challenge"); //Not an actual error, just for the color
                        } else {
                            error(p, "You have no open challenges by " + target.getName() + "!");
                        }
                    }

                } else {
                    sendUsage(p);
                }
            } else {
                sendUsage(p);
            }
        } else {
            error(p, "You are currently ingame!");
        }
    }
}
