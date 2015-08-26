/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.command;

import java.util.Arrays;
import java.util.regex.Pattern;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class BasicCommand implements CommandExecutor {
    
    protected CorePlugin plugin;
    protected String name;
    protected Rank requiredRank;
    protected Rank[] additionalRanks;
    protected boolean hasCommandBlockExecutor = false;
    private String[] usages = null;
    public static final String NO_COMMAND_PERMISSION_MESSAGE = "You don't have permission to use this command!";
    public static final String PLAYERDATA_ERROR_MESSAGE = "Your player data hasn't yet been loaded. Please try again.";
    public static final String NO_PLAYER_INSTANCE = Theme.WARNING.buildTheme(false) + "This command can only be run by an instance of a player.";
    
    public BasicCommand(CorePlugin plugin, String name, String usage) {
        this(plugin, name, usage, Rank.DEFAULT);
    }

    public BasicCommand(CorePlugin plugin, String name, String usage, Rank requiredRank, Rank... additionalRanks) {
        this.plugin = plugin;
        this.name = name;
        this.requiredRank = requiredRank;
        this.additionalRanks = additionalRanks;
        usage = usage.replaceAll(Pattern.quote("<command>"), name);
        this.usages = StringUtils.split(usage, "\n");
        plugin.getCommand(name).setExecutor(this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        try {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
                if (slp != null) {
                    if (slp.getRank().hasPermission(requiredRank) || Arrays.asList(additionalRanks).contains(slp.getRank())) {
                        run(p, slp, cmd, args);
                    } else {
                        error(sender, NO_COMMAND_PERMISSION_MESSAGE);
                    }
                } else {
                    error(sender, PLAYERDATA_ERROR_MESSAGE);
                }
            } else if (sender instanceof ConsoleCommandSender) {
                runConsole(sender, cmd, args);
            } else if (sender instanceof BlockCommandSender) {
                if (hasCommandBlockExecutor) {
                    runBlock(sender, cmd, args);
                } else {
                    runConsole(sender, cmd, args);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    protected void error(CommandSender cs, String message) {    
        cs.sendMessage(plugin.getChatPrefix() + " " + Theme.ERROR.buildTheme(false) + message);
    }

    protected void success(CommandSender cs, String message) {
        cs.sendMessage(plugin.getChatPrefix() + " " + Theme.SUCCESS.buildTheme(false) + message);
    }

    protected void sendUsage(CommandSender cs) {
        cs.sendMessage(plugin.getChatPrefix() + " " + Theme.ERROR.buildTheme(false) + "Correct Usage: ");
        for (String m : usages) {
            cs.sendMessage(plugin.getChatPrefix() + " " + Theme.INCOGNITO.buildTheme(false) + m);
        }
    }
    
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        cs.sendMessage(plugin.getChatPrefix() + " " + NO_PLAYER_INSTANCE);
    }

    protected int runBlock(CommandSender cs, Command cmd, String[] args) {
        cs.sendMessage(plugin.getChatPrefix() + " " + NO_PLAYER_INSTANCE);
        return 0;
    }
    
    protected abstract void run(Player p, SLPlayer slp, Command cmd, String[] args);

}
