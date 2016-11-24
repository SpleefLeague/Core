package com.spleefleague.core.utils;

import com.spleefleague.core.SpleefLeague;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Константин
 */
public abstract class Task extends BukkitRunnable {
    
    public final static HashMap<String, Task> tasks = new HashMap<>();
        
    private int periods, delay, period;
    private final String name;
    private final JavaPlugin plugin;

    public Task(JavaPlugin plugin, String name, int periods, int delayInMilliseconds, int periodInMilliseconds) {
        if(delayInMilliseconds != 0 && delayInMilliseconds < 50)
            throw new IllegalArgumentException("Delay time must be 0 or not less than 50ms!");
        if(periodInMilliseconds < 50)
            throw new IllegalArgumentException("Period time must be not less than 50ms!");
        this.name = name;
        this.plugin = plugin;
        this.periods = periods;
        if(periods == 0)
            periods = -1;
        delay = delayInMilliseconds / 50;
        period = periodInMilliseconds / 50;
        runTaskTimer(plugin, delay, period);
        tasks.put(name, this);
    }

    public abstract void onTick();

    @Override
    public void run() {
        if(periods > 0)
            --periods;
        onTick();
        if(periods == 0)
            cancel();
    }

    public int getPeriods() {
        return periods;
    }

    public int getDelayInTicks() {
        return delay;
    }

    public int getPeriodInTicks() {
        return period;
    }

    public String getName() {
        return name;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }
    
    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(getName());
    }
    
    public static Task getTask(String name) {
        return tasks.get(name);
    }
    
    public static void schedule(Runnable r) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SpleefLeague.getInstance(), r);
    }
    
    public static void schedule(Runnable r, long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SpleefLeague.getInstance(), r, delay);
    }
    
    public static void schedule(Runnable r, long delay, long period) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefLeague.getInstance(), r, delay, period);
    }
    
}
