package com.spleefleague.core.utils;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.Settings;
import java.util.List;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class AutoBroadcaster {
    
    private static int index = 0;
    private static List<String> notices;
    
    private static long interval = 60;

    public static void init() {
        MongoCursor<Document> docs = SpleefLeague.getInstance().getPluginDB().getCollection("AutoBroadcasts").find().iterator();
        while(docs.hasNext())
            notices.add(ChatColor.translateAlternateColorCodes('&', docs.next().getString("message")));
        if(Settings.hasKey("autobroadcaster_interval"))
            interval = Settings.getInteger("autobroadcaster_interval");
        interval *= 20l;
        if(notices.isEmpty())
            return;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefLeague.getInstance(), () -> {
            Bukkit.broadcastMessage(notices.get(index++));
            if(index == notices.size())
                index = 0;
        }, 0l, interval);
    }
    
}
