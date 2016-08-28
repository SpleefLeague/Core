package com.spleefleague.core.utils;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author RinesThaix
 */
public class UtilChat {
    
    /**
     * Broadcast message.
     * @param message message to broadcast
     */
    public static void b(Object message) {
        Bukkit.broadcastMessage(c(message.toString()));
    }
    
    /**
     * Broadcast message.
     * @param message message to format & broadcast
     * @param args arguments for string formatting
     */
    public static void b(String message, Object... args) {
        b(String.format(message, args));
    }
    
    /**
     * Broadcast message with prefix made by given plugin name.
     * @param plugin name of the plugin
     * @param message message to broadcast
     */
    public static void pb(String plugin, String message) {
        b("&7[&6%s&7] &e%s", plugin, message);
    }
    
    /**
     * Broadcast message with prefix made by given plugin name.
     * @param plugin name of the plugin
     * @param message message to format & broadcast
     * @param args arguments for string formatting
     */
    public static void pb(String plugin, String message, Object... args) {
        pb(plugin, String.format(message, args));
    }
    
    /**
     * Color given string (replace color codes).
     * @param message message to color
     * @return colored message
     */
    public static String c(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Color given string (replace color codes).
     * @param message message to format & color
     * @param args arguments for string formatting
     * @return colored message
     */
    public static String c(String message, Object... args) {
        return c(String.format(message, args));
    }
    
    public static String pc(String plugin, String message) {
        return c("&7[&6%s&7] &e%s", plugin, message);
    }
    
    public static String pc(String plugin, String message, Object... args) {
        return c("&7[&6%s&7] &e%s", plugin, String.format(message, args));
    }
    
    /**
     * Strip all colors from the string.
     * @param message message
     * @return stripped message
     */
    public static String s(String message) {
        return ChatColor.stripColor(message);
    }
    
    /**
     * Send colored message to the given player.
     * @param p message receiver
     * @param message message to send
     */
    public static void s(Player p, String message) {
        p.sendMessage(c(message));
    }
    
    /**
     * Send formatted colored message to the given player.
     * @param p message receiver
     * @param message message to format & send
     * @param args arguments for string formatting
     */
    public static void s(Player p, String message, Object... args) {
        s(p, String.format(message, args));
    }
    
    /**
     * Send formatted colored message to the given player with prefix made by given plugin name.
     * @param plugin name of the plugin
     * @param p message receiver
     * @param message message to format & send
     * @param args arguments for string formatting
     */
    public static void ps(String plugin, Player p, String message, Object... args) {
        s(p, "&7[&6%s&7] &e%s", plugin, String.format(message, args));
    }
    
    /**
     * Sens formatted colored message to the given command sender with prefix made by given plugin name.
     * @param plugin name of the plugin
     * @param cs message receiver
     * @param message message to format & send
     * @param args arguments for string formatting
     */
    public static void ps(String plugin, CommandSender cs, String message, Object... args) {
        cs.sendMessage(UtilChat.c("&7[&6%s&7] &e%s", plugin, String.format(message, args)));
    }
    
    /**
     * Send colored message to the given player.
     * @param cs message receiver
     * @param message message to send
     */
    public static void s(CommandSender cs, String message) {
        cs.sendMessage(c(message));
    }
    
    /**
     * Send formatted colored message to the given player.
     * @param cs message receiver
     * @param message message to format & send
     * @param args arguments for string formatting
     */
    public static void s(CommandSender cs, String message, Object... args) {
        s(cs, String.format(message, args));
    }
    
    /**
     * Debug given message.
     * @param message message to format and debug
     * @param args arguments for string formatting
     */
    public static void debug(String message, Object... args) {
        debug(String.format(message, args));
    }
    
    /**
     * Debug given object.
     * @param obj object to print in debug
     */
    public static void debug(Object obj) {
        pb("Debug", obj.toString());
        System.out.println(obj.toString());
    }
    
    /**
     * Debug given number, usually used as debug-points.
     * @param num number to print in debug
     */
    public static void debug(int num) {
        pb("Debug", "#%d", num);
        System.out.println(num);
    }
    
    public static HoverEvent makeHoverEvent(List<String> lore) {
        BaseComponent[] comps = new BaseComponent[lore.size()];
        for(int i = 0; i < lore.size(); ++i)
            comps[i] = new TextComponent(c("%s%s", lore.get(i), i == lore.size() - 1 ? "" : "\n"));
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, comps);
    }
    
    public static TextComponent makeTextComponentUncolored(String text) {
        return new TextComponent(TextComponent.fromLegacyText(text));
    }
    
    public static TextComponent makeTextComponent(String text) {
        return makeTextComponentUncolored(c(text));
    }
    
    public static TextComponent makeTextComponent(String text, Object... args) {
        return makeTextComponentUncolored(c(text, args));
    }
    
    public static void s(Theme theme, Player p, String msg) {
        p.sendMessage(SpleefLeague.getInstance().getChatPrefix() + " " + theme.buildTheme(false) + UtilChat.c(msg));
    }
    
    public static void s(Theme theme, Player p, String msg, Object... args) {
        s(theme, p, String.format(msg, args));
    }
    
}
