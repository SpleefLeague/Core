package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.plugin.GamePlugin;
import com.spleefleague.core.utils.UtilChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class version extends BasicCommand {

    public version(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEFAULT);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        runConsole(p, cmd, args);
    }
    
    @Override
    protected void runConsole(CommandSender cs, Command cmd, String[] args) {
        UtilChat.s(cs, "");
        String commitId = SpleefLeague.getInstance().getCommitId();
        if(commitId.equals("unknown"))
            UtilChat.s(cs, "&7This server is running &funknown &7version of SpleefLeagueCore.");
        else {
            String subversion = commitId.substring(0, 7);
            UtilChat.s(cs, "&7This server is running SpleefLeagueCore version &f#%s&7.", subversion.toUpperCase());
            UtilChat.s(cs, "&7Last update: &f%s&7.", SpleefLeague.getInstance().getCommitDate());
        }
        UtilChat.s(cs, "");
        for(GamePlugin plugin : GamePlugin.getGamePlugins()) {
            commitId = plugin.getCommitId();
            if(commitId.equals("unknown"))
                UtilChat.s(cs, "&7There's a %s game plugin of &funknown &7version.", plugin.getName());
            else {
                String subversion = commitId.substring(0, 7);
                UtilChat.s(cs, "&7%s plugin version: &f%s&7.", plugin.getName(), subversion.toUpperCase());
                UtilChat.s(cs, "&7Last update: &f%s&7.", plugin.getCommitDate());
            }
            UtilChat.s(cs, "");
        }
        UtilChat.s(cs, "&7All sources are available at &fhttps://github.com/SpleefLeague/");
        UtilChat.s(cs, "");
    }
    
}
