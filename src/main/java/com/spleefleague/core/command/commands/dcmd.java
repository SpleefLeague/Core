package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class dcmd extends BasicCommand {

    private final int commandsPerPage = 8;

    public dcmd(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void runConsole(CommandSender p, Command cmd, String[] args) {
        // usages
        // add <haste>
        // enable <name>
        // remove <name>
        // list [page]
        // disabled [page]
        if (args.length < 1) {
            sendUsage(p);
            return;
        }
        String subCmd = args[0].toLowerCase();
        if (subCmd.equals("list")) {
            list(p, args);
        } else if (subCmd.equals("disabled")) {
            disabled(p, args);
        } else if (subCmd.equals("add") && args.length >= 2) {
            add(p, args);
        } else if (subCmd.equals("remove") && args.length >= 2) {
            remove(p, args);
        } else if (subCmd.equals("enable") && args.length >= 2) {
            enable(p, args);
        } else {
            sendUsage(p);
        }
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        this.runConsole(p, cmd, args);
    }

    public void list(CommandSender p, String[] args) {
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception x) {
                sendUsage(p);
                return;
            }
        }
        String[] commands = SpleefLeague.getInstance().getDynamicCommandManager().getRegisteredCommands();
        if (commands.length == 0) {
            p.sendMessage(ChatColor.RED + "No Dynamic Commands are currently loaded");
            return;
        }
        page--;
        int low = (page * commandsPerPage);
        int high = low + commandsPerPage;
        if (low < 0) {
            low = 0;
            high = low + commandsPerPage;
        }
        while (low >= commands.length) {
            low -= commandsPerPage;
        }
        if (high >= commands.length) {
            high = commands.length;
        }
        page = low / commandsPerPage;
        int totalPages = commands.length / commandsPerPage;
        page++;
        totalPages++;
        p.sendMessage(ChatColor.GRAY + "[==== " + ChatColor.GREEN + "Enabled Dynamic Commands [page " + page + "/" + totalPages + "]" + ChatColor.GRAY + " ====]");
        for (int i = low; i < high; i++) {
            p.sendMessage(ChatColor.DARK_GREEN + " - /" + ChatColor.GREEN + commands[i]);
        }
    }

    public void disabled(CommandSender p, String[] args) {
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception x) {
                sendUsage(p);
                return;
            }
        }
        String[] commands = SpleefLeague.getInstance().getDynamicCommandManager().getUnloadedCommands();
        if (commands.length == 0) {
            p.sendMessage(ChatColor.RED + "No Dynamic Commands are currently unloaded");
            return;
        }
        page--;
        int low = (page * commandsPerPage);
        int high = low + commandsPerPage;
        if (low < 0) {
            low = 0;
            high = low + commandsPerPage;
        }
        while (low >= commands.length) {
            low -= commandsPerPage;
        }
        if (high >= commands.length) {
            high = commands.length;
        }
        page = low / commandsPerPage;
        int totalPages = commands.length / commandsPerPage;
        page++;
        totalPages++;
        p.sendMessage(ChatColor.GRAY + "[==== " + ChatColor.RED + "Disabled Dynamic Commands [page " + page + "/" + totalPages + "]" + ChatColor.GRAY + " ====]");
        for (int i = low; i < high; i++) {
            p.sendMessage(ChatColor.DARK_RED + " - /" + ChatColor.RED + commands[i]);
        }
    }

    public void add(final CommandSender p, String[] args) {
        String haste = args[1];
        SpleefLeague.getInstance().getDynamicCommandManager().register(
                haste,
                (ChatColor color, String txt) -> {
                    p.sendMessage(color + txt);
                }
        );
    }

    public void remove(CommandSender p, String[] args) {
        if (SpleefLeague.getInstance().getDynamicCommandManager().unregister(args[1])) {
            success(p, "Unregistered dynamic command: " + args[1]);
        } else {
            error(p, "Command not found");
        }
    }
    
    public void enable(CommandSender p, String[] args) {
        String name = args[1];
        SpleefLeague.getInstance().getDynamicCommandManager().enable(
                name,
                (ChatColor color, String txt) -> {
                    p.sendMessage(color + txt);
                }
        );
    }

}
