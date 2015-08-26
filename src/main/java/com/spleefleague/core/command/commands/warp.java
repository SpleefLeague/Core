package com.spleefleague.core.command.commands;

import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.utils.Warp;

public class warp extends BasicCommand{

	public warp(CorePlugin plugin, String name, String usage) {
		super(plugin, name, usage,Rank.MODERATOR,Rank.BUILDER);
	}

	@Override
	protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
		
		
		if(args.length == 0){
			sendWarpsList(p);
		}else if(args.length == 1){
			teleportToWarp(p, args[0]);
		}else{
			sendUsage(p);
		}	
	}

	
	private void sendWarpsList(Player p){		
		List<Warp> warps = Warp.getAll().stream().sorted((w1,w2) -> w1.getName().compareTo(w2.getName())).collect(Collectors.toList());
		
		ComponentBuilder cb = new ComponentBuilder("---------------Warps list---------------").color(ChatColor.RED);
		cb.append("\n").reset();
		
		
		boolean first = true;
		
		for(Warp warp : warps){
			if(!first)
				cb.append(" , ").reset();
			
			cb.append("[" + warp.getName() + "]")
				.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getName()))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport to '" + warp.getName() +"'").create()));
	
			first = false;
		}
		
		
		p.spigot().sendMessage(cb.create());
	}
	

	
	
	private void teleportToWarp(Player p, String warpName){
		Warp warp = Warp.byName(warpName);
		
		if(warp != null){
			
			//TODO: check if ingame?
			p.teleport(warp.getLocation());
			success(p, "You have been teleported to '" + warp.getName() + "'");
		}else{
			error(p, "The specified warp does not exist");
		}
	}
}
