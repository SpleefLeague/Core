/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityZombie;
import net.minecraft.server.v1_8_R1.Navigation;
import net.minecraft.server.v1_8_R1.PathEntity;
import net.minecraft.server.v1_8_R1.PathfinderGoal;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class ControllableVillager extends EntityZombie {

    private final PathfinderGoalWalkToLoc pathfinderWalk;
    private final PathfinderGoalLookAtSpecificPlayer pathfinderLook;
    private final Location location;
    private final Player player;
    
    public ControllableVillager(Location location, Player player) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.location = location;
        this.player = player;
        List goalB = (List)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        List goalC = (List)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        List targetB = (List)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        List targetC = (List)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        pathfinderWalk = new PathfinderGoalWalkToLoc(this);
        pathfinderLook = new PathfinderGoalLookAtSpecificPlayer(this);
        pathfinderLook.setTarget(player);
        this.goalSelector.a(0, pathfinderWalk);
        this.goalSelector.a(1, pathfinderLook);
        super.setCustomName("Tutorial");
        super.setCustomNameVisible(true);
    }

    public void walkTo(Location location) {
        pathfinderWalk.setGoal(location);
    }
    
    public void lookAt(Player player) {
        pathfinderLook.setTarget(player);
    }
    
    public void spawn() {
        EntityTypes.spawnEntity(this, location);
    }
    
    @Override
    public void collide(Entity e) {}
    
    private static class PathfinderGoalWalkToLoc extends PathfinderGoal {

        private final double speed = 1.5;
        private final ControllableVillager entity;
        private final Navigation navigation;
        private Location loc;
        private boolean changed = false;
        
        public PathfinderGoalWalkToLoc(ControllableVillager entity) {
            this.entity = entity;
            this.navigation = (Navigation) this.entity.getNavigation();
        }
        
        protected void setGoal(Location goal) {
            this.loc = goal;
            this.changed = true;
        }
        
        @Override
        public boolean a() {
            return loc != null && entity.player.getLocation().distance(entity.getBukkitEntity().getLocation()) < 10;
        }
        
        @Override
        public boolean b() {
            boolean b;
            if(changed) {
                changed = false;
                b = false;
            }
            else {
                boolean a = a();
                b = a;
            }
            if(!b) {
                navigation.a((PathEntity)null, speed);
            }
            return b;
        }
        
        @Override
        public void c() {
            PathEntity pe = navigation.a(loc.getX(), loc.getY(), loc.getZ());
            entity.getNavigation().a(pe, speed);
        }
    }
        
    private static class PathfinderGoalLookAtSpecificPlayer extends PathfinderGoal {

        private final ControllableVillager entity;
        private Entity target;
        
        public PathfinderGoalLookAtSpecificPlayer(ControllableVillager entity) {
            this.entity = entity;
        }
        
        public void setTarget(Player player) {
            this.target = ((CraftPlayer)player).getHandle();
        }
        
        @Override
        public boolean a() {
            return target != null;
        }
        
        @Override
        public void e() {
            if(a())
                this.entity.getControllerLook().a(this.target.locX, this.target.locY + (double) this.target.getHeadHeight(), this.target.locZ, 10.0F, (float)this.entity.bP());
        }
    }

    private static Object getPrivateField(String fieldName, Class c, Object object) {
        Field field;
        Object o = null;
        try {
            field = c.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public enum EntityTypes {
        CUSTOM_VILLAGER("Villager", org.bukkit.entity.EntityType.VILLAGER.getTypeId(), ControllableVillager.class); //You can add as many as you want.

        private EntityTypes(String name, int id, Class<? extends Entity> custom) {
            addToMaps(custom, name, id);
        }

        public static void spawnEntity(Entity entity, Location loc) {
            entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            ((CraftWorld) loc.getWorld()).getHandle().addEntity(entity);
        }

        private static void addToMaps(Class clazz, String name, int id) {
            ((Map) getPrivateField("d", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, name);
            ((Map) getPrivateField("f", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, id);
        }
    }
}
