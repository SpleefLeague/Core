/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;
import net.spleefleague.core.utils.DatabaseLookup;
import net.spleefleague.core.utils.TypeConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class GeneralPlayer {
    
    private String username;
    private UUID uuid;
    
    protected GeneralPlayer() {
        
    }
    
    public GeneralPlayer(UUID uuid) {
        this.uuid = uuid;
        this.username = DatabaseLookup.getUsername(uuid);
    }
    
    public GeneralPlayer(String username) {
        this.uuid = DatabaseLookup.getUUID(username);
        this.username = username;
    }
    
    public GeneralPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }
    
    @DBSave(columnName = "uuid")
    public UUID getUUID() {
        return uuid;
    }
    
    @DBSave(columnName = "username")
    public String getUsername() {
        return username;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    protected void setUsername(String username) {
        this.username = username;
    }
    
    protected void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void sendMessage() {
        
    }
    
    protected void load(DB db) {
        HashMap<String, Method> methods = new HashMap<>();
        try {
            Class current = this.getClass();
            while(GeneralPlayer.class.isAssignableFrom(current)) {
                for(Method m : current.getDeclaredMethods()) {
                    DBLoad annotation = m.getAnnotation(DBLoad.class);
                    if(annotation != null) {
                        methods.put(annotation.fieldName(), m);
                    }
                }
                current = current.getSuperclass();
            }
            DBObject dbo = db.getCollection("Players").findOne(new BasicDBObject("uuid", uuid.toString()));
            if(dbo == null) {
                setDefaults();
                save(db);
            }
            else {
                for(String key : dbo.keySet()) {
                    Method m = methods.get(key);
                    if(m != null) {
                        Object o = dbo.get(key);
                        if(m.getParameterTypes()[0].isEnum() && o instanceof String) {
                            m.invoke(this, Enum.valueOf((Class<Enum>)m.getParameterTypes()[0], (String)o));
                        }
                        else if(m.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                            TypeConverter tc = m.getAnnotation(DBLoad.class).typeConverter().newInstance();
                            m.invoke(this, tc.convert(o));
                        }
                        else {
                            m.invoke(this, o);
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void save(DB db) {
        
    }
    
    abstract void setDefaults();
}
