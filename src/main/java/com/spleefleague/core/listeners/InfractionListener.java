/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listeners;

import java.time.Duration;
import java.time.Instant;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.EntityBuilder;
import com.spleefleague.core.utils.TimeUtil;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.infraction.InfractionType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 *
 * @author Manuel
 */
public class InfractionListener implements Listener{
    
    private static Listener instance;
    
    public static void init() {
        if(instance == null) {
            instance = new InfractionListener();
            Bukkit.getPluginManager().registerEvents(instance, SpleefLeague.getInstance());
        }
    }
    
    private InfractionListener() {
        
    }
    
    @EventHandler
    public void banCheck(AsyncPlayerPreLoginEvent e){
        Document dbo = SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").find(new Document("uuid", e.getUniqueId().toString())).first();
        if(dbo == null){
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
        }
        else{
            Infraction inf = EntityBuilder.load(dbo, Infraction.class);
            if(inf.getType() == InfractionType.BAN) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage("You have been banned for: " + inf.getMessage());
            }
            else if(inf.getType() == InfractionType.TEMPBAN){
                if(inf.getTime() + inf.getDuration() <= System.currentTimeMillis()){
                    SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").deleteOne(new Document("uuid", e.getUniqueId().toString()));
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
                }
                else {
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    e.setKickMessage("You have been tempbanned for " + TimeUtil.durationToString(Duration.between(Instant.now(), Instant.ofEpochMilli(inf.getTime() + inf.getDuration()))) + ". " + inf.getMessage());
            
                }
            }
        }
    }
}
