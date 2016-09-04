package com.spleefleague.core.utils;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;
import org.bukkit.Bukkit;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class AutoBroadcaster {

    public static final Long DEFAULT_INTERVAL = 60L;
    public static final String MESSAGE_FIELD = "message", INTERVAL_SETTING = "autobroadcaster_interval";

    private List<String> notices;
    private int index, taskID;
    private long interval;

    public AutoBroadcaster(MongoCollection<Document> collection) {
        this.notices = new ArrayList<>();
        collection.find().forEach((Consumer<? super Document>) (Document document) -> {
            this.notices.add(document.getString(MESSAGE_FIELD));
        });
        interval = (Settings.hasKey(INTERVAL_SETTING) ? Settings.getInteger(INTERVAL_SETTING) : DEFAULT_INTERVAL);
        interval *= 20L;
        if(!notices.isEmpty()) {
            initTask();
        }
    }

    /**
     * Begin the broadcasting task.
     */
    private void initTask() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefLeague.getInstance(), () -> {
            Bukkit.broadcastMessage(notices.get(index++));
            if(index == notices.size()) {
                index = 0;
            }
        }, 0L, interval);
    }

    /**
     * Stop the auto broadcaster.
     */
    public void stopTask() {
        Bukkit.getScheduler().cancelTask(taskID);
    }
    
}
