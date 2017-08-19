package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.io.Config;
import com.spleefleague.core.io.connections.ConnectionClient;
import com.spleefleague.core.io.connections.ConnectionResponseHandler;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import com.sun.scenario.Settings;
import org.bukkit.Bukkit;
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
            ConnectionClient cc = SpleefLeague.getInstance().getConnectionClient();
            String message = StringUtil.fromArgsArray(args, 1);
            if(cc.isEnabled()) {
                Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                    SLPlayer target = SpleefLeague.getInstance().getPlayerManager().loadFake(args[0]);
                    if(target == null) {
                        error(p, args[0] + " has never played on SpleefLeague!");
                        return;
                    }
                    JSONObject request = new JSONObject();
                    request.put("uuid", target.getUniqueId().toString());
                    request.put("action", "GET_PLAYER");
                    new ConnectionResponseHandler("sessions", request, 40) {

                        @Override
                        protected void response(JSONObject jsonObject) {
                            if(jsonObject == null || jsonObject.get("playerServer").toString().equalsIgnoreCase("OFFLINE")) {
                                error(slp, target.getName() + " isn't online!");
                                return;
                            }
                            JSONObject sendObject = new JSONObject();
                            sendObject.put("rankColor", slp.getRank().getColor().name());
                            sendObject.put("sendUUID", target.getUniqueId());
                            sendObject.put("sendName", target.getName());
                            sendObject.put("shownName", slp.getName());
                            sendObject.put("overrideServer", jsonObject.get("playerServer").toString());
                            sendObject.put("message", message);
                            SpleefLeague.getInstance().getConnectionClient().send("ticket", sendObject);
                        }

                    };
                });
            }
            else {
                SLPlayer target = SpleefLeague.getInstance().getPlayerManager().get(args[0]);
                if(target == null) {
                    error(slp, args[0] + " isn't online!");
                    return;
                }
                ticket ticketCommand = (ticket)SpleefLeague.getInstance().getBasicCommand("ticket");
                ticketCommand.sendTicket(target.getName(), slp.getName(), target.getUniqueId(), message, Config.getString("server_name"), slp.getRank().getColor());
            }
        } else {
            sendUsage(p);
        }
    }
}
