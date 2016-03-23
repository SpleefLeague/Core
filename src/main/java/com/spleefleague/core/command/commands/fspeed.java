package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 * Created by Josh on 07/02/2016.
 */
public class fspeed extends BasicCommand {

    public fspeed(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR, Rank.BUILDER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length != 1) {
            sendUsage(slp);
            return;
        }
        float speed;
        try {
            speed = Float.valueOf(args[0]);
        } catch (Exception e) {
            error(slp, "Please enter a number between 1 and 10!");
            return;
        }
        speed = speed / 10;
        if (speed < 0.1 || speed > 1.0) {
            error(slp, "Please enter a number between 1 and 10!");
            return;
        }
        slp.setFlySpeed(speed);
        success(slp, "Flyspeed set to " + args[0] + "!");
    }

}
