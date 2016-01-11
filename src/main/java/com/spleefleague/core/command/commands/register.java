/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.XenforoAPIUtil;
import com.spleefleague.core.utils.XenforoAPIUtil.Result;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class register extends BasicCommand {

    public register(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length == 1) {
            if(!slp.hasForumAccount()) {
                Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                    Result result = XenforoAPIUtil.createForumUser(p, args[0]);
                    switch(result) {
                        case SUCCESS: {
                            slp.setForumAccount(true);
                            success(p, "Your account has been created.");
                            break;
                        }
                        case INVALID_EMAIL: {
                            error(p, "Please enter a valid email address!");
                            break;
                        }
                        case INJECTION: {
                            error(p, "Please enter a valid email address! This incident has been logged.");
                            break;
                        }
                        case EMAIL_EXISTS: {
                            error(p, "This email address already exists!");
                            break;
                        }
                        case UNREACHABLE: {
                            error(p, "The website seems to be unreachable. Please try again later.");
                            break;
                        }
                    }
                });
            }
            else {
                error(p, "You already seem to have an account. If you believe this is an error, contact the staff team.");
            }
        }
        else {
            sendUsage(slp);
        }
    }
}
