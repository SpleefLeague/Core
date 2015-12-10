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
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.StringUtil;
import com.spleefleague.core.utils.TimeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

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
            SLPlayer target;
            if (targetPlayer != null) {
                target = SpleefLeague.getInstance().getPlayerManager().get(targetPlayer);
            }
            else {
                target = SpleefLeague.getInstance().getPlayerManager().loadFake(targetName);
                if (target == null) {
                    error(p, targetName + " does not exist.");
                    return;
                }
            }
            PlayerData data = new PlayerData(target);
            p.sendMessage(ChatColor.DARK_GRAY + "[========== " + ChatColor.GRAY + targetName + "'s data" + ChatColor.DARK_GRAY + "==========]");
            p.sendMessage(ChatColor.DARK_GRAY + "Name: " + ChatColor.GRAY + data.getName());
            p.sendMessage(ChatColor.DARK_GRAY + "UUID: " + ChatColor.GRAY + data.getUUID());
            p.sendMessage(ChatColor.DARK_GRAY + "Rank: " + ChatColor.GRAY + data.getRank());
            p.sendMessage(ChatColor.DARK_GRAY + "State: " + data.getState());
            if(target.isOnline()) {
                p.sendMessage(ChatColor.DARK_GRAY + "IP: " + ChatColor.GRAY + data.getIP());
            }
            else {
                p.sendMessage(ChatColor.DARK_GRAY + "Last seen: " + ChatColor.GRAY + data.getLastSeen());    
            }
            String sharedAccounts = data.getSharedAccounts();
            if(sharedAccounts != null) p.sendMessage(ChatColor.DARK_GRAY + "Shared accounts: " + ChatColor.GRAY + sharedAccounts);
        }
        else {
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
            return slp.getRank().getColor() + slp.getRank().getDisplayName();
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
            if(lastSeen == null) {
                getSharedAccounts();
            }
            return lastSeen;
        }

        public String getState() {
            if (slp.isOnline()) {
                Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").find(new Document("uuid", slp.getUniqueId().toString())).first();
                if (dbo != null) {
                    Infraction inf = EntityBuilder.load(dbo, Infraction.class);
                    if (inf.getType() == InfractionType.BAN) {
                        return ChatColor.DARK_RED + "BANNED";
                    }
                    else if (inf.getType() == InfractionType.TEMPBAN) {
                        if (inf.getTime() + inf.getDuration() > System.currentTimeMillis()) {
                            return ChatColor.RED + "TEMPBANNED" + ChatColor.GRAY + " (for " + TimeUtil.dateToString(new Date(inf.getTime() + inf.getDuration()), true) + ")";
                        }
                    }
                }
                return ChatColor.GRAY + "OFFLINE";
            }
            else {
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
                if(lastOnline == null) {
                    lastOnline = doc.get("date", Date.class);
                }
                else {
                    Date d = doc.get("date", Date.class);
                    if(lastOnline.before(d)) lastOnline = d;
                }
            }
            lastSeen = lastOnline != null ? TimeUtil.dateToString(lastOnline, false) + " ago" : "Unknown";
            List<Document> orQuerry = new ArrayList<>();
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
            for(String uuid : sharedUUIDs) {
                if(sharedUsernames == null) {
                    sharedUsernames = col.find(new Document("uuid", uuid)).first().get("username", String.class);
                }
                else {
                    sharedUsernames += ", " + col.find(new Document("uuid", uuid)).first().get("username", String.class);
                }
            }
            return sharedUsernames;
        }
    }
}
