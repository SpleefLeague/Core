/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.command.commands;

import net.spleefleague.core.plugin.CorePlugin;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Manuel
 */
public class gamemode extends BasicCommand{

    public gamemode(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.DEVELOPER, Rank.BUILDER);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        GameMode mode = null;
        Player player = p;
        if(args.length == 0){
            sendUsage(p);
            return;
        }
        if(args.length >= 1){
            try{
                mode = GameMode.valueOf(args[0].toUpperCase());
            }
            catch(IllegalArgumentException e){
                try{
                    mode = GameMode.getByValue(Integer.parseInt(args[0]));
                }
                catch(NumberFormatException ex){
                    error(p, "The gamemode \"" + args[0] + "\" doesn't exist!");
                    return;
                }
            }
        }
        if(args.length >= 2){
            if((player = Bukkit.getPlayerExact(args[1])) == null){
                error(p, "The player \"" + args[1] + "\" is not online!");
                return;
            }
        }
        player.setGameMode(mode);
        success(player, "Your gamemode has been updated!");
    }
}
