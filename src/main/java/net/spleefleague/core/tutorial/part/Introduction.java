/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.tutorial.part;

import com.mongodb.BasicDBObject;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.io.DBEntity;
import net.spleefleague.core.io.DBLoad;
import net.spleefleague.core.io.DBLoadable;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.Tutorial;
import net.spleefleague.core.tutorial.TutorialPart;
import net.spleefleague.core.utils.ControllableVillager;
import net.spleefleague.core.io.EntityBuilder;
import net.spleefleague.core.utils.TypeConverter.LocationConverter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 *
 * @author Jonas
 */
public class Introduction extends TutorialPart {

    private static final Metadata meta;
    
    public Introduction(SLPlayer gp, Tutorial tutorial) {
        super(gp, tutorial);
    }

    @Override
    public void onPlayerMessage(String message) {
        if(currentStep <= 1) {
            if(message.equalsIgnoreCase("yes") || message.equalsIgnoreCase("y") || message.equalsIgnoreCase("ja")) {
                cancelMessages(true);
                sendMessages(new String[]{"That's nice to hear!"}, true);
            }
            else if(message.equalsIgnoreCase("no") || message.equalsIgnoreCase("n") || message.equalsIgnoreCase("nein")) {
                cancelMessages(true);
                sendMessages(new String[]{"If you ever need help, just enter /tutorial :)"}, false);
                Tutorial.getTutorial(getPlayer()).end(true);
            }
        }
    }
    
    @Override
    public void start() {
        this.getPlayer().getPlayer().teleport(meta.playerSpawn);
        spawnVillager();
        String[] messages = new String[]{
            "Welcome on SpleefLeague!",
            "My name is Villager #4 and I'm going to explain you how this server works.",
            "If you want to see this tutorial, write \"yes\", otherwise write \"no\""
        };
        sendMessages(messages, true);
    }
    
    private void spawnVillager() {
        meta.villagerSpawn.setDirection(new Vector(meta.playerSpawn.getX() - meta.villagerSpawn.getX(), meta.playerSpawn.getY() - meta.villagerSpawn.getY(), meta.playerSpawn.getZ() - meta.villagerSpawn.getZ()));
        Location l = meta.villagerSpawn;
        ControllableVillager cv = new ControllableVillager(l, getPlayer().getPlayer());
        cv.spawn();
        tutorial.setEntity(cv);
        cv.walkTo(cv.getBukkitEntity().getLocation().add(6, 0, 6));
    }
    
    static {
        meta = EntityBuilder.load(SpleefLeague.getInstance().getPluginDB().getCollection("Tutorials").findOne(new BasicDBObject("name", "Introduction")), Metadata.class);
    }
    
    public static class Metadata extends DBEntity implements DBLoadable{
        
        private Location playerSpawn, villagerSpawn;
        
        @DBLoad(fieldName = "playerSpawn", typeConverter = LocationConverter.class)
        public void setPlayerSpawn(Location spawn) {
            this.playerSpawn = spawn;
        }
        
        @DBLoad(fieldName = "villagerSpawn", typeConverter = LocationConverter.class)
        public void setVillagerSpawn(Location spawn) {
            this.villagerSpawn = spawn;
        }
    }
}
