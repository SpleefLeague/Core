package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 *
 * @author Patrick F.
 */
public class ticket extends BasicCommand {

    public ticket(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEFAULT);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length > 0) {
            String message = StringUtil.fromArgsArray(args);
            JSONObject sendObject = new JSONObject();
            sendObject.put("rankColor", slp.getRank().getColor().name());
            sendObject.put("sendUUID", p.getUniqueId());
            sendObject.put("sendName", p.getName());
            sendObject.put("shownName", p.getName());
            sendObject.put("message", message);
            SpleefLeague.getInstance().getConnectionClient().send("ticket", sendObject);
        } else {
            sendUsage(p);
        }
    }
}
