/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import org.bukkit.ChatColor;

/**
 *
 * @author Jonas
 */
public enum Rank {
    
    ADMIN(100, "Admin", ChatColor.DARK_RED),
    COUNCIL(100, "Council", ChatColor.DARK_RED),
    DEVELOPER(90, "Dev", ChatColor.BLUE),
    SENIOR_MODERATOR(80, "Sr. Mod", ChatColor.RED),
    MODERATOR(60, "Mod", ChatColor.RED),
    VIP(40, "VIP", ChatColor.DARK_PURPLE),
    PREMIUM(30, "Premium", ChatColor.GOLD),
    RETARD(10, "Retard", ChatColor.LIGHT_PURPLE),
    DEFAULT(0, "Default", ChatColor.YELLOW);
    
    private final int permission;
    private final String name;
    private final ChatColor color;
    
    private Rank(int permission, String name, ChatColor color) {
        this.permission = permission;
        this.name = name;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }
    
    public ChatColor getColor() {
        return color;
    }
    
    public boolean hasPermission(Rank rank) {
        return permission >= rank.permission;
    }
}