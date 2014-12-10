/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.PlayerState;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.Tutorial;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class tutorial extends BasicCommand{

    public tutorial(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(!Tutorial.isInTutorial(slp)) {
            if(slp.getState() == PlayerState.IDLE) {
                Tutorial t = new Tutorial(slp);
                t.start();
            }
            else {
                error(p, "You are currently " + slp.getState().name().toLowerCase() + "!");
            }
        }
        else {
            error(p, "You are already in a tutorial!");
        }
    }
}
