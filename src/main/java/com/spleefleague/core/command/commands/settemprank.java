package com.spleefleague.core.command.commands;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.DatabaseConnection;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class settemprank extends BasicCommand {

    public settemprank(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }

    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if (args.length == 3) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if (player != null) {
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(player.getUniqueId());
                if (slp != null) {
                    Rank rank;
                    try {
                        rank = Rank.valueOf(args[1].toUpperCase());
                    } catch (Exception e) {
                        rank = null;
                    }
                    if (rank != null) {
                        int hours = -1;
                        try {
                            hours = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex) {
                        }
                        if (hours <= -1) {
                            error(cs, "Amount of hours must be a positive integer.");
                            return;
                        }
                        if (hours == 0) {
                            slp.setEternalRank(rank);
                            slp.setExpiringRank(rank, 0l);
                        } else {
                            long endingTime = System.currentTimeMillis() + hours * 60 * 60 * 1000l;
                            slp.setExpiringRank(rank, endingTime);
                        }
                        success(cs, "Rank has been set.");
                    } else {
                        error(cs, "The rank " + args[1] + " does not exist!");
                    }
                } else {
                    setRankOffline(cs, args[0], args[1], args[2]);
                }
            } else {
                setRankOffline(cs, args[0], args[1], args[2]);
            }
        } else {
            sendUsage(cs);
        }
    }

    private void setRankOffline(CommandSender cs, String name, String r, String h) {
        Rank rank = Rank.valueOf(r);
        if (rank == null) {
            error(cs, "The rank " + r + " does not exist!");
        } else {
            int hours = -1;
            try {
                hours = Integer.parseInt(h);
            } catch (NumberFormatException ex) {
            }
            if (hours <= -1 && hours != -17) {
                error(cs, "Amount of hours must be a positive integer.");
                return;
            }
            MongoCollection<Document> collection = SpleefLeague.getInstance().getPluginDB().getCollection("Players");
            Document index = new Document("username", name);
            Pair<String, Object> prank = Pair.<String, Object>of("rank", rank.getName());
            if (hours == 0) {
                DatabaseConnection.updateFields(collection,
                        index,
                        prank,
                        Pair.<String, Object>of("eternalRank", rank.getName()),
                        Pair.<String, Object>of("rankExpirationTime", 0l)
                );
            } else if (hours == -17) { //testing case
                long endingTime = System.currentTimeMillis();
                DatabaseConnection.updateFields(collection,
                        index,
                        prank,
                        Pair.<String, Object>of("rankExpirationTime", endingTime + 20000)
                );
            } else {
                long endingTime = System.currentTimeMillis() + hours * 60 * 60 * 1000l;
                DatabaseConnection.updateFields(collection,
                        index,
                        prank,
                        Pair.<String, Object>of("rankExpirationTime", endingTime)
                );
            }
            success(cs, "Attempting rank update for offline player.");
        }
    }
}
