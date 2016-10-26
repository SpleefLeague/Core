package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CosmeticsManager;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.UtilChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class collectibles extends BasicCommand {

    public collectibles(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            UtilChat.s(Theme.INFO, cs, "Help for &b/collectibles&e:");
            printHelp(cs, "/collectibles list <page>", "prints list of all cosmetic items with their ids");
            printHelp(cs, "/collectibles give <player> <id>", "gives cosmetic item with given id to the player");
            printHelp(cs, "/collectibles take <player> <id>", "takes cosmetic item with given id from the player");
            return;
        }
        switch(args[0].toLowerCase()) {
            case "list": {
                int page = 1;
                if(args.length == 2)
                    try {
                        page = Integer.parseInt(args[1]);
                    }catch(NumberFormatException ex) {}
                final int perPage = 10;
                int start = (page - 1) * perPage + 1, end = page * perPage;
                for(int id = start; id <= end; ++id) {
                    CItem citem = CosmeticsManager.getItem(id);
                    if(citem == null)
                        break;
                    UtilChat.s(cs, "&7%d. &r%s", id, citem.getName());
                }
                break;
            }case "give": {
                if(args.length != 3) {
                    error(cs, "Not enough arguments!");
                    break;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null) {
                    error(cs, "This command doesn't work for offline players.");
                    break;
                }
                int id = 0;
                try {
                    id = Integer.parseInt(args[2]);
                }catch(NumberFormatException ex) {}
                if(id == 0) {
                    error(cs, "Id of cosmetic item must be a positive integer.");
                    break;
                }
                CItem citem = CosmeticsManager.getItem(id);
                if(citem == null) {
                    error(cs, "Cosmetic item with given id doesn't exist.");
                    break;
                }
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(target);
                if(slp.getCollectibles().getItems().contains(id)) {
                    error(cs, "This player already has cosmetic item with given id.");
                    break;
                }
                slp.getCollectibles().addItem(id);
                UtilChat.s(Theme.SUCCESS, cs, "New cosmetic item with id &b%d &ahas been added to &b%s&a!", id, slp.getName());
                break;
            }case "take": {
                if(args.length != 3) {
                    error(cs, "Not enough arguments!");
                    break;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null) {
                    error(cs, "This command doesn't work for offline players.");
                    break;
                }
                int id = 0;
                try {
                    id = Integer.parseInt(args[2]);
                }catch(NumberFormatException ex) {}
                if(id == 0) {
                    error(cs, "Id of cosmetic item must be a positive integer.");
                    break;
                }
                CItem citem = CosmeticsManager.getItem(id);
                if(citem == null) {
                    error(cs, "Cosmetic item with given id doesn't exist.");
                    break;
                }
                SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(target);
                if(!slp.getCollectibles().getItems().contains(id)) {
                    error(cs, "This player doesn't have cosmetic item with given id.");
                    break;
                }
                slp.getCollectibles().removeItem(id);
                UtilChat.s(Theme.SUCCESS, cs, "Cosmetic item with id &b%d &ahas been taken from &b%s&a!", id, slp.getName());
                break;
            }default:
                UtilChat.s(Theme.ERROR, cs, "There's no such a subcommand for &b/collectibles&c.");
                break;
        }
    }
    
    private void printHelp(CommandSender cs, String cmd, String description) {
        UtilChat.s(cs, "&b%s &8- &7%s&8.", cmd, description);
    }
    
}
