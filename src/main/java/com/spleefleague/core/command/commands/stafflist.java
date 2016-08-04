package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.io.connections.ConnectionResponseHandler;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by Josh on 04/08/2016.
 */
public class stafflist extends BasicCommand {

    public stafflist(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        success(p, "Requesting...");

        JSONObject requestObject = new JSONObject();
        requestObject.put("action", "GET_STAFF");
        new ConnectionResponseHandler("sessions", requestObject, 100) {

            @Override
            protected void response(JSONObject jsonObject) {
                if(jsonObject == null) {
                    error(p, "Response timed out! Please try again.");
                    return;
                }
                p.sendMessage(ChatColor.DARK_GRAY + "[========== " + ChatColor.DARK_AQUA + "Online Staff " + ChatColor.DARK_GRAY + " ==========]");

                JSONArray result = (JSONArray) jsonObject.get("staff");
                for (Object results : result) {
                    JSONObject staffMember = (JSONObject) results;
                    String name = (String) staffMember.get("username"), server = (String) staffMember.get("playerServer");
                    Rank rank = Rank.valueOf(staffMember.get("rank").toString().toUpperCase());
                    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + (rank == null ? "Unknown" : rank.getDisplayName())
                            + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + name + ChatColor.DARK_GRAY + " | "
                            + ChatColor.GREEN + "Online on " + server + "!");
                }
            }

        };
    }

}
