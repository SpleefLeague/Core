/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.menus;

import org.bukkit.entity.Player;

/**
 *
 * @author jonas
 * @param <T>
 */
public interface PlayerBuilder<T extends Player> {
    
    public void setPlayer(T p);
    public T getPlayer();
}
