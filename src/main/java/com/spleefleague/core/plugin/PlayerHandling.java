package com.spleefleague.core.plugin;

import com.mongodb.client.MongoDatabase;
import org.bukkit.entity.Player;

/**
 *
 * @author jonas
 */
public interface PlayerHandling {
    
    MongoDatabase getPluginDB(); 
    void syncSave(Player p);
}
