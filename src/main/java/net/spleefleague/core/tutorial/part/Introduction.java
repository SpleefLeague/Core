/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.tutorial.part;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.player.SLPlayer;
import net.spleefleague.core.tutorial.Tutorial;
import net.spleefleague.core.tutorial.TutorialPart;
import net.spleefleague.core.utils.ControllableVillager;
import net.spleefleague.core.utils.EntityBuilder;
import net.spleefleague.core.utils.TypeConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
    public void onComplete() {
        
    }

    @Override
    public void onCancel() {
    
    }
    
    @Override
    public void onPlayerMessage(String message) {
        System.out.println(currentStep + message);
        if(currentStep == 1) {
            if(message.equalsIgnoreCase("yes") || message.equalsIgnoreCase("y") || message.equalsIgnoreCase("ja")) {
                sendMessages(new String[]{"That's nice to hear!"}, true);
            }
            else if(message.equalsIgnoreCase("no") || message.equalsIgnoreCase("n") || message.equalsIgnoreCase("nein")) {
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
        tutorial.setEntity(cv.getBukkitEntity());
        cv.walkTo(cv.getBukkitEntity().getLocation().add(6, 0, 6));
    }
    
    static {
        meta = EntityBuilder.load(SpleefLeague.getInstance().getPluginDB().getCollection("Tutorials").findOne(new BasicDBObject("name", "Introduction")), Metadata.class);
    }
    
    public static class Metadata {
        
        private Location playerSpawn, villagerSpawn;
        
        @DBLoad(fieldName = "playerSpawn", typeConverter = LocationConverter.class)
        public void setPlayerSpawn(Location spawn) {
            this.playerSpawn = spawn;
        }
        
        @DBLoad(fieldName = "villagerSpawn", typeConverter = LocationConverter.class)
        public void setVillagerSpawn(Location spawn) {
            this.villagerSpawn = spawn;
        }
        
        public static class LocationConverter extends TypeConverter<BasicDBList, Location> {

            @Override
            public Location convertLoad(BasicDBList t) {
                double x, y, z;
                World world;
                x = (double)(Integer)t.get(0);
                y = (double)(Integer) t.get(1);
                z = (double)(Integer) t.get(2);
                world = (t.size() == 4) ? Bukkit.getWorld((String)t.get(3)) : SpleefLeague.DEFAULT_WORLD;
                return new Location(world, x, y, z);
            }

            @Override
            public BasicDBList convertSave(Location v) {
                BasicDBList bdbl = new BasicDBList();
                bdbl.add(v.getX());
                bdbl.add(v.getY());
                bdbl.add(v.getZ());
                if(v.getWorld() != SpleefLeague.DEFAULT_WORLD) {
                    bdbl.add(v.getWorld().getName());
                }
                return bdbl;
            }
        }
    }
}
