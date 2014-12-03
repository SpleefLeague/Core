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
import net.spleefleague.core.utils.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 *
 * @author Jonas
 */
public class Introduction extends TutorialPart {

    private static final Metadata meta;
    
    public Introduction(SLPlayer gp, int entityID) {
        super(gp, entityID);
    }

    @Override
    public void onComplete() {
        
    }

    @Override
    public void onCancel() {
    
    }
    
    @Override
    public void onPlayerMessage(String message) {
        
    }
    
    @Override
    public void start() {
        this.getPlayer().getPlayer().teleport(meta.playerSpawn);
        spawnVillager(entityID);
        String[] messages = new String[]{
            "Hello! My name is Villager #4 and I'm going to explain you how SpleefLeague works.",
        };
        sendMessages(messages, true);
    }
    
    private void spawnVillager(int entityID) {
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setType(EntityType.VILLAGER);
        meta.villagerSpawn.setDirection(new Vector(meta.playerSpawn.getX() - meta.villagerSpawn.getX(), meta.playerSpawn.getY() - meta.villagerSpawn.getY(), meta.playerSpawn.getZ() - meta.villagerSpawn.getZ()));
        packet.setX(meta.villagerSpawn.getX());
        packet.setY(meta.villagerSpawn.getY());
        packet.setZ(meta.villagerSpawn.getZ());
        packet.setHeadPitch(meta.villagerSpawn.getPitch());
        packet.setHeadYaw(meta.villagerSpawn.getYaw());
        packet.setEntityID(entityID);
        packet.sendPacket(getPlayer().getPlayer());
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
