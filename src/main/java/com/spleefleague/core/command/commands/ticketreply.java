package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.io.connections.ConnectionResponseHandler;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 *
 * @author Patrick F.
 */
public class ticketreply extends BasicCommand {

    public ticketreply(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length > 1) {
            Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                SLPlayer slPlayer = SpleefLeague.getInstance().getPlayerManager().loadFake(args[0]);
                if(slPlayer == null) {
                    error(p, args[0] + " has never played on SpleefLeague!");
                    return;
                }
                JSONObject request = new JSONObject();
                request.put("uuid", slPlayer.getUniqueId().toString());
                request.put("action", "GET_PLAYER");
                new ConnectionResponseHandler("sessions", request, 40) {

                    @Override
                    protected void response(JSONObject jsonObject) {
                        if(jsonObject == null || jsonObject.get("playerServer").toString().equalsIgnoreCase("OFFLINE")) {
                            error(slp, slPlayer.getName() + " isn't online!");
                            return;
                        }
                        String message = StringUtil.fromArgsArray(args, 1);
                        JSONObject sendObject = new JSONObject();
                        sendObject.put("rankColor", slp.getRank().getColor().name());
                        sendObject.put("sendUUID", slPlayer.getUniqueId());
                        sendObject.put("sendName", slPlayer.getName());
                        sendObject.put("shownName", slp.getName());
                        sendObject.put("overrideServer", jsonObject.get("playerServer").toString());
                        sendObject.put("message", message);
                        SpleefLeague.getInstance().getConnectionClient().send("ticket", sendObject);
                    }

                };
            });
        } else {
            sendUsage(p);
        }
    }
}
