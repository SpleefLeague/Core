/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
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
import java.time.Duration;
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
import java.util.concurrent.Semaphore;
import org.bson.conversions.Bson;
import org.bukkit.scheduler.BukkitTask;

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
                        Bukkit.getScheduler().runTaskAsynchronously(SpleefLeague.getInstance(), () -> {
                            PlayerData data = new PlayerData(target);
                            data.calculate();
                            
                            TextComponent uuidFirst = new TextComponent("UUID: ");
                            uuidFirst.setColor(ChatColor.DARK_GRAY);
                            TextComponent uuidSecond = new TextComponent(data.getUUID());
                            uuidSecond.setColor(ChatColor.GRAY);
                            uuidSecond.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to put in chatbox") } ));
                            uuidSecond.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, data.getUUID()));
                            uuidFirst.addExtra(uuidSecond);
                            
                            p.sendMessage(ChatColor.DARK_GRAY + "[========== " + ChatColor.GRAY + targetName + "'s data " + ChatColor.DARK_GRAY + "==========]");
                            p.sendMessage(ChatColor.DARK_GRAY + "Name: " + ChatColor.GRAY + data.getName());
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
                            List<String> sharedNames = data.getSharedAccountNames();
                            if(!sharedNames.isEmpty()) {
                                StringJoiner sj = new StringJoiner(", ");
                                for(String name : data.getSharedAccountNames()) {
                                    sj.add(name);
                                }   
                                p.sendMessage(ChatColor.DARK_GRAY + "Shared accounts: " + ChatColor.GRAY + sj.toString());
                            }
                            if(data.getOnlineTime() != null) {
                                p.sendMessage(ChatColor.DARK_GRAY + "Total online time: " + ChatColor.GRAY + data.getOnlineTime());
                            }
                        });
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
        private String state;
        private String onlineTime;
        private List<String> sharedAccountNames;

        public PlayerData(SLPlayer slp) {
            this.slp = slp;
        }
        
        public void calculate() {
            SpleefLeague sl = SpleefLeague.getInstance();
            Semaphore s = new Semaphore(0);
            Runnable calcLastSeen = () -> {
                lastSeen = calculateLastSeen();
                s.release();
            };
            Runnable calcState = () -> {
                state = calculateState();
                s.release();
            };
            Runnable calcOnlineTime = () -> {
                onlineTime = calculateOnlineTime();
                s.release();
            };
            Runnable calcSharedAccountNames = () -> {
                sharedAccountNames = calculateSharedAccountNames();
                s.release();
            };
            Bukkit.getScheduler().runTaskAsynchronously(sl, calcLastSeen);
            Bukkit.getScheduler().runTaskAsynchronously(sl, calcState);
            Bukkit.getScheduler().runTaskAsynchronously(sl, calcOnlineTime);
            Bukkit.getScheduler().runTaskAsynchronously(sl, calcSharedAccountNames);
            s.acquireUninterruptibly(4);
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
            return lastSeen;
        }

        public String getState() {
            return state;
        }

        public String getOnlineTime() {
            return onlineTime;
        }

        public List<String> getSharedAccountNames() {
            return sharedAccountNames;
        }
        
        private String calculateState() {
            if (!slp.isOnline()) {
                Document query = new Document("uuid", slp.getUniqueId().toString());
                Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").find(query).first();
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
        
        private String calculateLastSeen() {
            MongoCollection<Document> col = SpleefLeague.getInstance().getPluginDB().getCollection("PlayerConnections");
            Document query = new Document("uuid", getUUID())
                    .append("type", "LEAVE");
            Document sort = new Document("date", -1);
            Bson projection = Projections.fields(Projections.excludeId(), Projections.include("date"));
            Document doc = col.find(query).sort(sort).projection(projection).limit(1).first();
            if(doc != null) {
                Date lastLogout = doc.getDate("date");
                return TimeUtil.dateToString(lastLogout, false) + " ago";
            }
            else {
                return "Unknown";
            }
        }
        
        private String calculateOnlineTime() {
            MongoCollection<Document> col = SpleefLeague.getInstance().getPluginDB().getCollection("PlayerConnections");
            Document query = new Document("uuid", getUUID());
            Document sort = new Document("date", 1);
            Bson projection = Projections.fields(Projections.excludeId(), Projections.include("date"), Projections.include("type"));
            FindIterable<Document> result = col.find(query).sort(sort).projection(projection);
            long delta = 0;
            boolean lastJoin = false;
            long lastJoinTime = 0;
            for(Document document : result) {
                if(document.getString("type").equals("LEAVE")) {
                    if(lastJoin) {
                        delta += document.getDate("date").getTime();
                        lastJoin = false;
                    }
                }
                else if(document.getString("type").equals("JOIN")) {
                    if(!lastJoin) {
                        long time = document.getDate("date").getTime();
                        delta -= time;
                        lastJoin = true;
                        lastJoinTime = time;
                    }
                }
            }
            if(slp.isOnline()) {
                delta += new Date().getTime();
            }
            else if(lastJoin) {
                delta += lastJoinTime;
            }
            if(delta > 0) {
                return TimeUtil.durationToString(Duration.ofMillis(delta));
            }
            else {
                return null;
            }
        }

        private List<String> calculateSharedAccountNames() {
            String playerUUID = slp.getUniqueId().toString();
            MongoCollection<Document> col = SpleefLeague.getInstance().getPluginDB().getCollection("PlayerConnections");
            List<Document> aggregation = generateAggregationPipeline(playerUUID);
            Document result = col.aggregate(aggregation).first();
            if(result == null) {
                return new ArrayList<>();
            }
            return (List<String>)result.get("shared");
        }
        
        private List<Document> generateAggregationPipeline(String uuid) {
            return Arrays.asList(new Document[]{
		new Document(
                    "$match", 
                    new Document("uuid", uuid)
		),
		new Document(
                    "$project",
                        new Document("_id", 0)
                        .append("ip", 1)
                        .append("uuid", 1)
		),
		new Document(
                    "$group",
                        new Document("_id", "$uuid")
                        .append("ip", 
                            new Document("$addToSet", "$ip")
                        )
		),
		new Document(
                    "$unwind", 
                        new Document("path", "$ip")
		),
		new Document(
                    "$lookup",
                        new Document("from", "PlayerConnections")
                        .append("localField", "ip")
                        .append("foreignField", "ip")
                        .append("as", "matching")
		),
                new Document(
                    "$unwind", 
                        new Document("path", "$matching")
		),
		new Document(
                    "$project",
                        new Document("_id", 0)
                        .append("uuid", "$matching.uuid")
                        .append("notOriginUUID", 
                            new Document("$cmp", Arrays.asList(new String[]{"$_id","$matching.uuid"}))
                        )
		),
                new Document(
                    "$match", 
                        new Document("notOriginUUID", 
                            new Document("$ne", 0)
                        )
		),
		new Document(
                    "$lookup",
                        new Document("from", "Players")
                        .append("localField", "uuid")
			.append("foreignField", "uuid")
			.append("as", "player")
				
		),
		new Document(
                    "$project",
                        new Document("username", 
                            new Document("$arrayElemAt", Arrays.asList(new Object[]{"$player.username",0}))
                        )
		),
		new Document(
                    "$group",
                        new Document("_id", "$_id")
                        .append("shared", 
                            new Document("$addToSet", "$username")
                        )
		),
		new Document(
                    "$project",
                        new Document("_id", 0)
                        .append("shared", 1)
		)
            });
        }
    }
}
