/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command;

import java.util.regex.Pattern;
import net.spleefleague.core.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.chat.Message;
import net.spleefleague.core.chat.Theme;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
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
    protected boolean hasCommandBlockExecutor = false;
    private String[] usages = null;
    private static final Message NO_COMMAND_PERMISSION_MESSAGE = new Message(Theme.ERROR, "You don't have permission to use this command!");
    private static final Message PLAYERDATA_ERROR_MESSAGE = new Message(Theme.ERROR, "Your player data hasn't yet been loaded. Please try again.");
    
    
    public BasicCommand(CorePlugin plugin, String name, String usage) {
        this(plugin, name, usage, Rank.DEFAULT);
    }

    public BasicCommand(CorePlugin plugin, String name, String usage, Rank requiredPermission) {
        this.plugin = plugin;
        this.name = name;
        this.requiredRank = requiredPermission;
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
                    if (slp.getRank().hasPermission(requiredRank)) {
                        run(p, slp, cmd, args);
                    } else {
                        NO_COMMAND_PERMISSION_MESSAGE.sendMessage(sender);
                    }
                } else {
                    PLAYERDATA_ERROR_MESSAGE.sendMessage(sender);
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
        
    }

    protected void success(CommandSender cs, String message) {
        Message.sendMessage(Theme.SUCCESS, message, cs);
    }

    protected void sendUsage(CommandSender cs) {
        Message.sendMessage(Theme.ERROR, "Correct Usage: ", cs);
        for (String m : usages) {
            Message.sendMessage(Theme.INCOGNITO, m, cs);
        }
    }
    
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        Message.sendMessage(Theme.WARNING, "This command can only be run by an instance of a player.", cs);
    }

    protected int runBlock(CommandSender cs, Command cmd, String[] args) {
        Message.sendMessage(Theme.WARNING, "This command can only be run by an instance of a player.", cs);
        return 0;
    }
    
    protected abstract void run(Player p, SLPlayer slp, Command cmd, String[] args);

}
