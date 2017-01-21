/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.infraction.InfractionType;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.io.connections.ConnectionResponseHandler;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import com.spleefleague.core.utils.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.*;

/**
 *
 * @author Jonas
 */
public class playerinfo extends BasicCommand {

    public playerinfo(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length < 2) {
            String targetName = (args.length == 0) ? p.getName() : args[0];
            Player targetPlayer = Bukkit.getPlayer(targetName);
            Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                SLPlayer target;
                if (targetPlayer != null) {
                    target = SpleefLeague.getInstance().getPlayerManager().get(targetPlayer);
                } else {
                    target = SpleefLeague.getInstance().getPlayerManager().loadFake(targetName);
                    if (target == null) {
                        error(p, targetName + " does not exist.");
                        return;
                    }
                }
                JSONObject request = new JSONObject();
                request.put("uuid", target.getUniqueId().toString());
                request.put("action", "GET_PLAYER");
                new ConnectionResponseHandler("sessions", request, 40) {

                    @Override
                    protected void response(JSONObject jsonObject) {
                        PlayerData data = new PlayerData(target);
                        p.sendMessage(ChatColor.DARK_GRAY + "[========== " + ChatColor.GRAY + targetName + "'s data " + ChatColor.DARK_GRAY + "==========]");
                        p.sendMessage(ChatColor.DARK_GRAY + "Name: " + ChatColor.GRAY + data.getName());
                        TextComponent uuidFirst = new TextComponent("UUID: ");
                        uuidFirst.setColor(ChatColor.DARK_GRAY);
                        TextComponent uuidSecond = new TextComponent(data.getUUID());
                        uuidSecond.setColor(ChatColor.GRAY);
                        uuidSecond.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to put in chatbox") } ));
                        uuidSecond.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, data.getUUID()));
                        uuidFirst.addExtra(uuidSecond);
                        p.spigot().sendMessage(uuidFirst);
                        p.sendMessage(ChatColor.DARK_GRAY + "Rank: " + ChatColor.GRAY + data.getRank());
                        if(data.getState().equalsIgnoreCase("OFFLINE") && jsonObject != null && !jsonObject.get("playerServer").toString().equalsIgnoreCase("OFFLINE")) {
                            p.sendMessage(ChatColor.DARK_GRAY + "State: ONLINE");
                        } else {
                            p.sendMessage(ChatColor.DARK_GRAY + "State: " + data.getState());
                        }
                        if (target.isOnline()) {
                            p.sendMessage(ChatColor.DARK_GRAY + "IP: " + ChatColor.GRAY + data.getIP());
                        } else {
                            p.sendMessage(ChatColor.DARK_GRAY + "Last seen: " + ChatColor.GRAY + data.getLastSeen());
                        }
                        p.sendMessage(ChatColor.DARK_GRAY + "Server: " + ChatColor.GRAY + (jsonObject == null ? "NONE (OFFLINE)" : jsonObject.get("playerServer").toString()));
                        String sharedAccounts = data.getSharedAccounts();
                        if (sharedAccounts != null) {
                            p.sendMessage(ChatColor.DARK_GRAY + "Shared accounts: " + ChatColor.GRAY + sharedAccounts);
                        }
                    }

                };
            });
        } else {
            sendUsage(p);
        }
    }

    private class PlayerData {

        private final SLPlayer slp;
        private String lastSeen;

        public PlayerData(SLPlayer slp) {
            this.slp = slp;
        }

        public String getRank() {
            Rank rank = slp == null || slp.getRank() == null ? Rank.DEFAULT : slp.getRank();
            return rank.getColor() + rank.getDisplayName();
        }

        public String getName() {
            return slp.getName();
        }

        public String getUUID() {
            return slp.getUniqueId().toString();
        }

        public String getIP() {
            return slp.getAddress().getAddress().toString().substring(1);
        }

        public String getLastSeen() {
            //Should never happen
            if (lastSeen == null) {
                getSharedAccounts();
            }
            return lastSeen;
        }

        public String getState() {
            if (!slp.isOnline()) {
                Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").find(new Document("uuid", slp.getUniqueId().toString())).first();
                if (dbo != null) {
                    Infraction inf = EntityBuilder.load(dbo, Infraction.class);
                    if (inf.getType() == InfractionType.BAN) {
                        return ChatColor.DARK_RED + "BANNED";
                    } else if (inf.getType() == InfractionType.TEMPBAN) {
                        if (inf.getTime() + inf.getDuration() > System.currentTimeMillis()) {
                            return ChatColor.RED + "TEMPBANNED" + ChatColor.GRAY + " (for " + TimeUtil.dateToString(new Date(inf.getTime() + inf.getDuration()), true) + ")";
                        }
                    }
                }
                return ChatColor.GRAY + "OFFLINE";
            } else {
                return ChatColor.GREEN + StringUtil.upperCaseFirst(slp.getState().toString());
            }
        }

        public String getSharedAccounts() {
            String playerUUID = slp.getUniqueId().toString();
            Set<String> sharedUUIDs = new HashSet<>();
            Collection<String> ips = new HashSet<>();
            MongoCollection<Document> col = SpleefLeague.getInstance().getPluginDB().getCollection("PlayerConnections");
            Date lastOnline = null;
            for (Document doc : col.find(new Document("uuid", playerUUID))) {
                ips.add(doc.get("ip", String.class));
                if (lastOnline == null) {
                    lastOnline = doc.get("date", Date.class);
                } else {
                    Date d = doc.get("date", Date.class);
                    if (lastOnline.before(d)) {
                        lastOnline = d;
                    }
                }
            }
            lastSeen = lastOnline != null ? TimeUtil.dateToString(lastOnline, false) + " ago" : "Unknown";
            Set<Document> orQuerry = new HashSet<>();
            for (String ip : ips) {
                orQuerry.add(new Document("ip", ip));
                Thread.currentThread().getStackTrace();
            }
            if (!orQuerry.isEmpty()) {
                for (Document doc : col.find(new Document("$or", orQuerry))) {
                    String uuid = doc.get("uuid", String.class);
                    if (!uuid.equals(playerUUID)) {
                        sharedUUIDs.add(uuid);
                    }
                }
            }
            col = SpleefLeague.getInstance().getPluginDB().getCollection("Players");
            String sharedUsernames = null;
            for (String uuid : sharedUUIDs) {
                if (sharedUsernames == null) {
                    sharedUsernames = col.find(new Document("uuid", uuid)).first().get("username", String.class);
                } else {
                    sharedUsernames += ", " + col.find(new Document("uuid", uuid)).first().get("username", String.class);
                }
            }
            return sharedUsernames;
        }
    }
}
