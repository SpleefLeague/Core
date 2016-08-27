package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 * Created by Josh on 09/08/2016.
 */
public class balancing extends BasicCommand {

    public balancing(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length == 0) {
            sendUsage(cs);
            return;
        }
        if(args[0].equalsIgnoreCase("refresh")) {
            JSONObject request = new JSONObject();
            request.put("action", "REFRESH");
            SpleefLeague.getInstance().getConnectionClient().send("rotation", request);
            success(cs, "Requested balancing refresh!");
        } else {
            sendUsage(cs);
        }
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        this.runConsole(p, cmd, args);
    }

}
