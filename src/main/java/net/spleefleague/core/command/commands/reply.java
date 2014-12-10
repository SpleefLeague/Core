/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import java.util.UUID;
import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class reply extends BasicCommand {

    public reply(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length > 0) {
            UUID lastChatpartner = slp.getLastChatPartner();
            if(lastChatpartner != null) {
                SLPlayer target = SpleefLeague.getInstance().getPlayerManager().get(Bukkit.getPlayer(lastChatpartner));
                if(target != null) {
                    String prefix1 = ChatColor.GRAY + "[me -> " + target.getRank().getColor() + target.getName() + ChatColor.GRAY + "] " + ChatColor.RESET;
                    String prefix2 = ChatColor.GRAY + "[" + slp.getRank().getColor() + slp.getName() + ChatColor.GRAY + " -> me] " + ChatColor.RESET;
                    String message = toMessage(args);
                    p.sendMessage(prefix1 + message);
                    target.getPlayer().sendMessage(prefix2 + message);
                    target.setLastChatPartner(slp.getUUID());
                }
                else {
                    error(p, args[0] + " is not online!");
                }
            }
            else {
                error(p, "You don't have anyone to reply to!");
            }
        }
        else {
            sendUsage(p);
        }
    }
    
    private String toMessage(String[] msg) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < msg.length; i++) {
            sb.append(msg[i]).append((i + 1 < msg.length) ? " " : "");
        }
        return sb.toString();
    }
}
