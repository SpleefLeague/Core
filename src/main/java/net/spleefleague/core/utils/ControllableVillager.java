/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.ChunkCache;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityVillager;
import net.minecraft.server.v1_8_R1.MathHelper;
import net.minecraft.server.v1_8_R1.Navigation;
import net.minecraft.server.v1_8_R1.NavigationAbstract;
import net.minecraft.server.v1_8_R1.PathEntity;
import net.minecraft.server.v1_8_R1.Pathfinder;
import net.minecraft.server.v1_8_R1.PathfinderGoal;
import net.minecraft.server.v1_8_R1.PathfinderGoalSelector;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public class ControllableVillager extends EntityVillager {

    private final PathfinderGoalWalkToLoc pathfinder;
    private final Location location;
    private final Player master;
    
    public ControllableVillager(Location location, Player master) {
        super(((CraftWorld) location.getWorld()).getHandle());
        this.location = location;
        this.master = master;
        List goalB = (List)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        List goalC = (List)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        List targetB = (List)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        List targetC = (List)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        pathfinder = new PathfinderGoalWalkToLoc(this);
//        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(0, pathfinder);
//        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 12.0F, 1.0F));
        super.setCustomName("Tutorial");
        super.setCustomNameVisible(true);
    }

    public void walkTo(Location location) {
        pathfinder.setGoal(location);
        
    }
    
    public void spawn() {
        EntityTypes.spawnEntity(this, location);
    }
    
    @Override
    public void collide(Entity e) {}
    
    private static class PathfinderGoalWalkToLoc extends PathfinderGoal {

        private final double speed = 1;
        private final ControllableVillager entity;
        private final Navigation navigation;
        private Location loc;
        
        public PathfinderGoalWalkToLoc(ControllableVillager entity) {
            this.entity = entity;
            this.navigation = (Navigation) this.entity.getNavigation();
        }
        
        protected void setGoal(Location goal) {
            this.loc = goal;
        }

        @Override
        public void c() {
            PathEntity pe = navigation.a(loc.getX(), loc.getY(), loc.getZ());//constructPathEntity();
            System.out.println("PathEntity: " + pe);
            if(pe == null) {
                System.out.println("Alternative: " + constructPathEntity());
            }
            System.out.println("Goal: " + loc.distance(entity.getBukkitEntity().getLocation()));
            this.navigation.a(pe, speed);
        }
        
        private PathEntity constructPathEntity() {
            try {
                BlockPosition paramBlockPosition = new BlockPosition(MathHelper.floor(loc.getX()), (int)loc.getY(), MathHelper.floor(loc.getZ()));
                float f1 = navigation.i();
                Field fc = NavigationAbstract.class.getDeclaredField("c");
                fc.setAccessible(true);
                net.minecraft.server.v1_8_R1.World c = (net.minecraft.server.v1_8_R1.World)fc.get(navigation);
                c.methodProfiler.a("pathfind");
                BlockPosition localBlockPosition = new BlockPosition(entity);
                int k = (int)(f1 + 8.0F);
                ChunkCache localChunkCache = new ChunkCache(c, localBlockPosition.a(-k, -k, -k), localBlockPosition.a(k, k, k), 0);
                Field fj = NavigationAbstract.class.getDeclaredField("j");
                fj.setAccessible(true);
                Pathfinder j = (Pathfinder)fj.get(navigation);
                j.a(localChunkCache, entity, paramBlockPosition, f1);
                PathEntity localPathEntity = j.a(localChunkCache, entity, paramBlockPosition, f1);
                c.methodProfiler.b();
                return localPathEntity;
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        @Override
        public boolean a() {
            return loc != null && entity.master.getLocation().distance(loc) < 10;
        }
    }

    private static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
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
        //getPrivateField is the method from above.
            //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
            //((Map)getPrivateField("c", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, clazz);
            ((Map) getPrivateField("d", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, name);
            //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
            ((Map) getPrivateField("f", net.minecraft.server.v1_8_R1.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
            //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
        }
    }
}
