package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 * Created by Josh on 11/08/2016.
 */
public class broadcast extends BasicCommand {

    public broadcast(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        this.runConsole(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length < 1) {
            sendUsage(cs);
            return;
        }
        String message = ChatColor.translateAlternateColorCodes('&', StringUtil.fromArgsArray(args));
        if(args[0].equalsIgnoreCase("local")) {
            message = message.replaceFirst("local", "").trim();
            Bukkit.broadcastMessage(String.format(SpleefLeague.BROADCAST_FORMAT, message));
        } else {
            JSONObject send = new JSONObject();
            send.put("message", message);
            SpleefLeague.getInstance().getConnectionClient().send("broadcast", send);
        }
    }
}
