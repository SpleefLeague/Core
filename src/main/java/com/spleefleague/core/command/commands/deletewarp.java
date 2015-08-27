package com.spleefleague.core.command.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.Warp;

public class deletewarp extends BasicCommand{

	public deletewarp(CorePlugin plugin, String name, String usage) {
		super(plugin, name, usage,Rank.MODERATOR,Rank.BUILDER);
	}

	@Override
	protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
		if(args.length == 1){
			Warp warp = Warp.byName(args[0]);
			if(warp != null){
				Warp.removeWarp(warp);
				success(p, "Warp '" + warp.getName() + "' deleted");
			}else{
				error(p, "The specified warp does not exist");
			}
		}else{
			sendUsage(p);
		}
	}

}