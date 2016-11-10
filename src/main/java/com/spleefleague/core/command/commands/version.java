package com.spleefleague.core.command.commands;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
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
        String commitId = SpleefLeague.getInstance().getCommitId();
        if(commitId.equals("unknown"))
            UtilChat.s(Theme.INFO, cs, "This server is running &cunknown &everion of SpleefLeagueCore.");
        else {
            String subversion = commitId.substring(0, 7);
            UtilChat.s(Theme.INFO, cs, "This server is running SpleefLeagueCore version &a#%s&e.", subversion);
            UtilChat.s(Theme.INFO, cs, "It was last time updated at &a%s&e.", SpleefLeague.getInstance().getCommitDate());
            UtilChat.s(Theme.INFO, cs, "The code can be obtained at &ahttps://github.com/SpleefLeague/Core/tree/%s&e.", commitId);
        }
    }
    
}
