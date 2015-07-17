/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command.commands;

import java.util.HashMap;
import java.util.UUID;
import net.spleefleague.core.command.BasicCommand;
import net.spleefleague.core.player.Rank;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class back extends BasicCommand {

    private HashMap<UUID, Location> lastLocations = new HashMap<>();
    
    public back(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(!lastLocations.containsKey(slp.getUUID())) {
            error(p, "There is no place to go back to.");
        }
        else {
            p.teleport(lastLocations.get(slp.getUUID()));
        }
    }
    
    public void setLastTeleport(Player player, Location location) {
        lastLocations.put(player.getUniqueId(), location);
    }
}
