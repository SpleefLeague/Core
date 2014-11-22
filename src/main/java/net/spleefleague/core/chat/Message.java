/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class Message {
    
    private Theme theme;
    private String message;
    
    public Message(Theme theme, String message) {
        this.theme = theme;
        this.message = message;
    }
    
    public Theme getTheme() {
        return theme;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    public void sendMessage(CommandSender sender) {
        sender.sendMessage(theme.buildTheme() + message);
    }
    
    public static void sendMessage(Theme theme, String message, CommandSender sender) {
        sender.sendMessage(theme.buildTheme() + message);
    }
}
