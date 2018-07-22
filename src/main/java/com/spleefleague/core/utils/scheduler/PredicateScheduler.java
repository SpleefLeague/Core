/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.scheduler;

import java.util.function.Function;
import java.util.function.IntPredicate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author jonas
 */
public class PredicateScheduler {
    
    public static BukkitTask runTaskTimer(Plugin plugin, Runnable run, long delay, long period, long amount) {
        return runTaskTimer(plugin, run, delay, period, i -> i < amount);
    }
    
    public static <T> BukkitTask runTaskTimer(Plugin plugin, Runnable run, long delay, long period, IntPredicate p) {
        return createTaskWrapper(r -> {
            return Bukkit.getScheduler().runTaskTimer(plugin, r, delay, period);
        }, run, p);
    }
    
    public static <T> BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable run, long delay, long period, long amount) {
        return runTaskTimerAsynchronously(plugin, run, delay, period, i -> i < amount);
    }
    
    public static <T> BukkitTask runTaskTimerAsynchronously(Plugin plugin, Runnable run, long delay, long period, IntPredicate p) {
        return createTaskWrapper(r -> {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, r, delay, period);
        }, run, p);
    }
    
    private static BukkitTask createTaskWrapper(Function<Runnable, BukkitTask> supplier, Runnable run, IntPredicate p) {
        return new BukkitTask() {
            
            private BukkitTask task;
            private int tick = 0;
            
            {
                task = supplier.apply(() -> {
                    if(!p.test(tick++)) {
                        task.cancel();
                    }
                    else {
                        run.run();
                    }
                });
            }
            
            @Override
            public int getTaskId() {
                return task.getTaskId();
            }

            @Override
            public Plugin getOwner() {
                return task.getOwner();
            }

            @Override
            public boolean isSync() {
                return task.isSync();
            }

            @Override
            public void cancel() {
                task.cancel();
            }

            @Override
            public boolean isCancelled() {
                return task.isCancelled();
            }
        };
    }
}
