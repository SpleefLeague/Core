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
import net.spleefleague.core.tutorial.TutorialPart;
import net.spleefleague.core.utils.EntityBuilder;
import net.spleefleague.core.utils.TypeConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Jonas
 */
public class Introduction extends TutorialPart {

    private static Metadata meta;
    private int currentStep;
    
    public Introduction(SLPlayer gp) {
        super(gp);
        this.currentStep = 0;
    }

    @Override
    public void onComplete() {
        
    }

    @Override
    public void onCancel() {
    
    }
    
    @Override
    public void start() {
        this.getPlayer().getPlayer().teleport(meta.playerSpawn);
        meta.villagerSpawn.getWorld().spawnEntity(meta.villagerSpawn, EntityType.VILLAGER);
    }
    
    static {
        meta = EntityBuilder.load(SpleefLeague.getInstance().getPluginDB().getCollection("Tutorials").findOne(new BasicDBObject("name", "Introduction")), Metadata.class);
    }
    
    private static class Metadata {
        
        private Location playerSpawn, villagerSpawn;
        
        @DBLoad(fieldName = "playerSpawn", typeConverter = LocationConverter.class)
        public void setPlayerSpawn(Location spawn) {
            this.playerSpawn = spawn;
        }
        
        @DBLoad(fieldName = "villagerSpawn", typeConverter = LocationConverter.class)
        public void setVillagerSpawn(Location spawn) {
            this.villagerSpawn = spawn;
        }
        
        public Location getPlayerSpawn() {
            return playerSpawn;
        }
        
        public Location getVillagerSpawn() {
            return villagerSpawn;
        }
        
        private static class LocationConverter extends TypeConverter<BasicDBList, Location> {

            @Override
            public Location convertLoad(BasicDBList t) {
                double x, y, z;
                World world;
                x = (double) t.get(0);
                y = (double) t.get(1);
                z = (double) t.get(2);
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
