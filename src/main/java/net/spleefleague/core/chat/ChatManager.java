/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.chat;

import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.player.SLPlayer;

/**
 *
 * @author Jonas
 */
public class ChatManager {
    
    public static void sendMessage(String message, String channel) {
        for(SLPlayer slp : SpleefLeague.getInstance().getPlayerManager().getAll()) {
            if(slp.isInChatChannel(channel)) {
                slp.getPlayer().sendMessage(message);
            }
        } 
    }
}