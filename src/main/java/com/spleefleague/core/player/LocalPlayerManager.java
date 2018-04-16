/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.plugin.CorePlugin;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author jonas
 * @param <G>
 */
public class LocalPlayerManager<G extends GeneralPlayer> extends PlayerManager<G> {
    
    public LocalPlayerManager(CorePlugin corePlugin, Class<G> playerClass) {
        super(corePlugin, playerClass);
        Bukkit.getOnlinePlayers().stream().forEach((player) -> {
            load(player);
        });
    }

    @Override
    protected void load(Player player) {
        try {
            G gp = playerClass.getConstructor().newInstance();
            gp.setName(player.getName());
            gp.setUUID(player.getUniqueId());
            gp.done();
            map.put(player.getUniqueId(), gp);
        } catch(IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void save(G player) {
        
    }
}
