/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.debug;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Debugger;
import org.bukkit.Bukkit;

/**
 *
 * @author jonas
 */
public class ChannelTest implements Debugger {
    
    @Override
    public void debug() {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get("Joba");
        Bukkit.broadcastMessage(slp.getSendingChannel().getName());
        Bukkit.broadcastMessage(""+slp.getOptions().isChatChannelEnabled(slp.getSendingChannel()));
        Bukkit.broadcastMessage(""+slp.getOptions().isChatChannelEnabled(ChatChannel.GLOBAL));
    }
}
