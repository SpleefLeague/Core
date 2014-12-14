/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command;

import java.util.regex.Pattern;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.SpleefLeague;
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
    private static final String NO_COMMAND_PERMISSION_MESSAGE = Theme.ERROR + "You don't have permission to use this command!";
    private static final String PLAYERDATA_ERROR_MESSAGE = Theme.ERROR + "Your player data hasn't yet been loaded. Please try again.";
    private static final String NO_PLAYER_INSTANCE = Theme.WARNING + "This command can only be run by an instance of a player.";
    
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
                        sender.sendMessage(NO_COMMAND_PERMISSION_MESSAGE);
                    }
                } else {
                    sender.sendMessage(PLAYERDATA_ERROR_MESSAGE);
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
        cs.sendMessage(Theme.ERROR + message);
    }

    protected void success(CommandSender cs, String message) {
        cs.sendMessage(Theme.SUCCESS + message);
    }

    protected void sendUsage(CommandSender cs) {
        cs.sendMessage(Theme.ERROR + "Correct Usage: ");
        for (String m : usages) {
            cs.sendMessage(Theme.INCOGNITO + m);
        }
    }
    
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        cs.sendMessage(NO_PLAYER_INSTANCE);
    }

    protected int runBlock(CommandSender cs, Command cmd, String[] args) {
        cs.sendMessage(NO_PLAYER_INSTANCE);
        return 0;
    }
    
    protected abstract void run(Player p, SLPlayer slp, Command cmd, String[] args);

}
