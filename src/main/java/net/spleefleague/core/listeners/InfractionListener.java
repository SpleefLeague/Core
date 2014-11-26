/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.spleefleague.core.listeners;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.utils.EntityBuilder;
import net.spleefleague.infraction.Infraction;
import net.spleefleague.infraction.InfractionType;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;

/**
 *
 * @author Manuel
 */
public class InfractionListener implements Listener{
    
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new InfractionListener(), SpleefLeague.getInstance());
    }
    
    public void banCheck(PlayerPreLoginEvent e){
        DBObject dbo = SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").findOne(new BasicDBObject("uuid", e.getUniqueId().toString()));
        if(dbo == null){
            e.setResult(PlayerPreLoginEvent.Result.ALLOWED);
        }
        else{
            Infraction inf = EntityBuilder.load(dbo, Infraction.class);
            if(inf.getType() == InfractionType.BAN)
                e.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
            else if(inf.getType() == InfractionType.TEMPBAN){
                if(inf.getTime() + inf.getDuration() <= System.currentTimeMillis()){
                    SpleefLeague.getInstance().getPluginDB().getCollection("ActiveInfractions").remove(new BasicDBObject("uuid", e.getUniqueId().toString()));
                    e.setResult(PlayerPreLoginEvent.Result.ALLOWED);
                }
                else
                    e.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
            }
        }
    }
}
