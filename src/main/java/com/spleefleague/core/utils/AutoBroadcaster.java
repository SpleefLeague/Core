package com.spleefleague.core.utils;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.SpleefLeague;
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
    
    private static long delay = 60;

    public static void init() {
        MongoCursor<Document> docs = SpleefLeague.getInstance().getPluginDB().getCollection("AutoBroadcaster_Notices").find().iterator();
        while(docs.hasNext())
            notices.add(ChatColor.translateAlternateColorCodes('&', docs.next().getString("message")));
        docs = SpleefLeague.getInstance().getPluginDB().getCollection("AutoBroadcaster_Delay").find().iterator();
        if(docs.hasNext())
            delay = docs.next().getInteger("delay");
        delay *= 20l;
        if(notices.isEmpty())
            return;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefLeague.getInstance(), () -> {
            Bukkit.broadcastMessage(notices.get(index++));
            if(index == notices.size())
                index = 0;
        }, 0l, delay);
    }
    
}
