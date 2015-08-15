/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.infraction;

import org.bukkit.ChatColor;

/**
 *
 * @author Manuel
 */
public enum InfractionType {
    WARNING("Warning", ChatColor.YELLOW),
    KICK("Kick", ChatColor.GOLD),
    TEMPBAN("Tempban", ChatColor.RED),
    BAN("Ban", ChatColor.DARK_RED),
    UNBAN("Unban", ChatColor.GREEN);
    
    private final String name;
    private final ChatColor color;
    
    private InfractionType(String name, ChatColor color){
        this.name = name;
        this.color = color;
    }
    
    public String getName(){
        return name;
    }
    public ChatColor getColor(){
        return color;
    }
}
