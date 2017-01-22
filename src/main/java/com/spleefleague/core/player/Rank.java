/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas
 */
public class Rank extends DBEntity implements DBLoadable {

    @DBLoad(fieldName = "name")
    private String name;
    @DBLoad(fieldName = "displayName")
    private String displayName;
    @DBLoad(fieldName = "ladder")
    private int ladder;
    @DBLoad(fieldName = "hasOp")
    private boolean hasOp;
    @DBLoad(fieldName = "color")
    private ChatColor color;
    @DBLoad(fieldName = "permissions")
    private String[] permissions;
    @DBLoad(fieldName = "exclusivePermissions")
    private String[] exclusivePermissions;
    private boolean assignedEnum = false;
    private Team scoreboardTeam;

    private Rank() {

    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLadder() {
        return ladder;
    }

    public boolean hasOp() {
        return hasOp;
    }

    public ChatColor getColor() {
        return color;
    }

    public boolean hasPermission(Rank rank) {
        return this == rank || this.getLadder() >= rank.getLadder();
    }

    public boolean hasPermission(String permission) {
        for (String perm : exclusivePermissions) {
            if (perm.equals(permission)) {
                return true;
            }
        }
        for (Rank rank : Rank.values()) {
            if (rank.getLadder() < this.getLadder()) {
                for (String perm : rank.permissions) {
                    if (perm.equals(permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<String> getAllPermissions() {
        List<String> permissions = new ArrayList<>(Arrays.asList(exclusivePermissions));
        for (Rank rank : Rank.values()) {
            if (rank.getLadder() < this.getLadder()) {
                permissions.addAll(Arrays.asList(rank.permissions));
            }
        }
        return permissions;
    }

    public Team getScoreboardTeam() {
        return this.scoreboardTeam;
    }

    private final static Map<String, Rank> ranks = new HashMap<>();

    public static final Rank ADMIN = getPlaceholderInstance(true),
            COUNCIL = getPlaceholderInstance(true),
            DEVELOPER = getPlaceholderInstance(true),
            SENIOR_MODERATOR = getPlaceholderInstance(true),
            MODERATOR_BUILDER = getPlaceholderInstance(true),
            MODERATOR = getPlaceholderInstance(true),
            REFEREE = getPlaceholderInstance(true),
            VIP = getPlaceholderInstance(true),
            BUILDER = getPlaceholderInstance(true),
            ORGANIZER = getPlaceholderInstance(true),
            DEFAULT = getPlaceholderInstance(true);

    public static Rank valueOf(String name) {
        return ranks.get(name);
    }

    public static Rank[] values() {
        return ranks.values().toArray(new Rank[0]);
    }

    private static Rank getPlaceholderInstance(boolean assignedEnum) {
        Rank rank = new Rank();
        rank.color = ChatColor.BLACK;
        rank.displayName = "ERROR";
        rank.name = "PLACEHOLDER";
        rank.hasOp = false;
        rank.ladder = Integer.MIN_VALUE;
        rank.exclusivePermissions = new String[0];
        rank.permissions = new String[0];
        rank.assignedEnum = assignedEnum;
        return rank;
    }

    public static void init() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Set<Team> teams = scoreboard.getTeams();
        teams.forEach(Team::unregister);
        MongoCursor<Document> dbc = SpleefLeague.getInstance().getPluginDB().getCollection("Ranks").find().iterator();
        while (dbc.hasNext()) {
            Rank rank = EntityBuilder.load(dbc.next(), Rank.class);
            Rank staticRank = getField(rank.getName());
            if (staticRank != null) {
                staticRank.name = rank.name;
                staticRank.displayName = rank.displayName;
                staticRank.hasOp = rank.hasOp;
                staticRank.ladder = rank.ladder;
                staticRank.color = rank.color;
                staticRank.permissions = rank.permissions;
                staticRank.exclusivePermissions = rank.exclusivePermissions;
                rank = staticRank;
            }
            Team t = scoreboard.registerNewTeam(normalizeRankName(rank.getName()));
            t.setDisplayName(t.getDisplayName());
            if (rank.getDisplayName().equalsIgnoreCase(Rank.DEFAULT.getDisplayName())) {
                t.setPrefix(rank.getColor().toString());
            } else {
                t.setPrefix(rank.getColor() + "[" + rank.getDisplayName() + "] ");
            }
            t.setSuffix(ChatColor.RESET.toString());
            rank.scoreboardTeam = t;
            ranks.put(rank.getName(), rank);
        }
        SpleefLeague.getInstance().log("Loaded " + ranks.size() + " ranks!");
    }

    private static Rank getField(String name) {
        try {
            Field field = Rank.class.getField(name);
            Rank staticRank = (Rank) field.get(null);
            return staticRank;
        } catch (NoSuchFieldException e) {
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Rank.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void managePermissions(Player player) {
        getAllPermissions().forEach(p -> player.addAttachment(SpleefLeague.getInstance(), p, true));
        player.setOp(hasOp);
    }

    public static class FromStringConverter extends TypeConverter<String, Rank> {

        public FromStringConverter() {
        }

        @Override
        public Rank convertLoad(String name) {
            return Rank.valueOf(name);
        }

        @Override
        public String convertSave(Rank rank) {
            return rank.getName();
        }
    }

    public static String normalizeRankName(String input) {
        if (input.length() <= 16) {
            return input;
        }
        if (input.contains("_")) {
            String[] parts = input.split("_");
            String output = "";
            for (String s : parts) {
                if (s.length() > 3) {
                    output += s.substring(0, 3);
                } else {
                    output += s;
                }
                output += "_";
            }
            output = output.substring(0, output.length() - 2);
            if (output.length() > 16) {
                return output.substring(0, 16);
            }
            return output;
        } else {
            return input.substring(0, 16);
        }
    }
}
