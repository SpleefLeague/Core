/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.player;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.SpleefLeague;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;
import net.spleefleague.core.utils.DatabaseConnection;
import net.spleefleague.core.utils.TypeConverter;
import net.spleefleague.core.utils.TypeConverter.UUIDStringConverter;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonas
 */
public abstract class GeneralPlayer {
    
    private String username;
    private UUID uuid;
    private HashMap<String, Method> loadMethods;
    private HashMap<String, Method> saveMethods;
    private ObjectId _id;
    private DBCollection dbcoll;
    private DBObject requestedSaves;
    private boolean isSaving;
    
    public GeneralPlayer() {
        this.requestedSaves = new BasicDBObject();
        this.isSaving = false;
        loadMethods();
    }
    
    @DBSave(fieldName = "uuid", typeConverter = UUIDStringConverter.class)
    public UUID getUUID() {
        return uuid;
    }
    
    @DBSave(fieldName = "username")
    public String getName() {
        return username;
    }
    
    @DBLoad(fieldName = "_id")
    public void setObjectId(ObjectId _id) {
        this._id = _id;
    }
    
    public ObjectId getObjectId() {
        return _id;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    protected void setName(String username) {
        this.username = username;
    }
    
    protected void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    protected void setDB(DB db) {
        this.dbcoll = db.getCollection("Players");
    }
    
    protected void load(DB db) {
        try {
            DBObject dbo = db.getCollection("Players").findOne(new BasicDBObject("uuid", uuid.toString()));
            if(dbo == null) {
                setDefaults();
                save(true);
            }
            else {
                for(String key : dbo.keySet()) {
                    Method m = loadMethods.get(key);
                    if(m != null) {
                        Object o = dbo.get(key);
                        if(m.getParameterTypes()[0].isEnum() && o instanceof String) {
                            m.invoke(this, Enum.valueOf((Class<Enum>)m.getParameterTypes()[0], (String)o));
                        }
                        else if(!m.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                            TypeConverter tc = m.getAnnotation(DBLoad.class).typeConverter().newInstance();
                            m.invoke(this, tc.convertLoad(o));
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
    
    public void requestSave(String field) {
        try {
            Method m = saveMethods.get(field);
            Object o;
            if (Enum.class.isAssignableFrom(m.getReturnType())) {
                o = m.invoke(this).toString();
            } else if (!m.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                TypeConverter tc = m.getAnnotation(DBSave.class).typeConverter().newInstance();
                o = tc.convertSave(m.invoke(this));
            } else {
                o = m.invoke(this);
            }
            requestedSaves.put(field, o);
            if(!isSaving) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(SpleefLeague.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        dbcoll.update(new BasicDBObject("_id", _id), requestedSaves);
                        isSaving = false;
                    }
                }, 200);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(GeneralPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void save() {
        save(false);
    }
    
    protected void save(String field) {
        try {
            DBObject dbo = new BasicDBObject();
            dbo.put(field, saveMethods.get(field).invoke(this));
            dbcoll.update(new BasicDBObject("_id", _id), new BasicDBObject("$set", dbo));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GeneralPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void save(boolean insert) {
        DBObject dbo = new BasicDBObject();
        for(String name : saveMethods.keySet()) {
            try {
                Method m = saveMethods.get(name);
                Object o = m.invoke(this);
                if(o != null) {
                    if (Enum.class.isAssignableFrom(m.getReturnType())) {
                        o = o.toString();
                    } else if (!m.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                        TypeConverter tc = m.getAnnotation(DBSave.class).typeConverter().newInstance();
                        o = tc.convertSave(o);
                    }
                    dbo.put(name, o);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                Logger.getLogger(GeneralPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(insert) {
            _id = (ObjectId)dbcoll.insert(dbo).getField("_id");
        }
        else {
            dbcoll.update(new BasicDBObject("_id", _id), new BasicDBObject("$set", dbo));
        }
    }
    
    private void loadMethods() {
        loadMethods = new HashMap<>();
        saveMethods = new HashMap<>();
        try {
            Class current = this.getClass();
            while(GeneralPlayer.class.isAssignableFrom(current)) {
                for(Method m : current.getDeclaredMethods()) {
                    DBLoad dbload = m.getAnnotation(DBLoad.class);
                    if(dbload != null) {
                        loadMethods.put(dbload.fieldName(), m);
                    }
                    DBSave dbsave = m.getAnnotation(DBSave.class);
                    if(dbsave != null) {
                        saveMethods.put(dbsave.fieldName(), m);
                    }
                    
                }
                current = current.getSuperclass();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    abstract void setDefaults();
}
