/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spleefleague.core.annotations.DBLoad;
import net.spleefleague.core.annotations.DBSave;
import net.spleefleague.core.player.GeneralPlayer;
import org.bson.types.ObjectId;

/**
 *
 * @author Jonas
 */
public class EntityBuilder {

    public static <T> void save(T object, DBCollection dbcoll, DBObject index, String field) {
        try {
            DBObject dbo = new BasicDBObject();
            dbo.put(field, getSaveMethods(object.getClass()).get(field).invoke(object));
            dbcoll.update(index, new BasicDBObject("$set", dbo));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GeneralPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static <T> ObjectId save(T object, DBCollection dbcoll, DBObject index) {
        try {
            HashMap<String, Method> saveMethods = getSaveMethods(object.getClass());
            HashMap<String, Field> saveFields = getSaveFields(object.getClass());
            DBObject set = new BasicDBObject();
            DBObject unset = new BasicDBObject();
            for(String name : saveMethods.keySet()) {
                try {
                    Method m = saveMethods.get(name);
                    if(m != null) {
                        m.setAccessible(true);
                        Object o = m.invoke(object);
                        if(o != null) {
                            if (Enum.class.isAssignableFrom(m.getReturnType())) {
                                o = o.toString();
                            } else if (!m.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                                TypeConverter tc = m.getAnnotation(DBSave.class).typeConverter().newInstance();
                                o = tc.convertSave(o);
                            }
                            set.put(name, o);
                        }
                        else {
                            unset.put(name, "");
                        }
                    }
                    else {
                        Field f = saveFields.get(name);
                        f.setAccessible(true);
                        Object o = f.get(object);
                        if(o != null) {
                            if (Enum.class.isAssignableFrom(o.getClass())) {
                                o = o.toString();
                            } else if (!f.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                                TypeConverter tc = f.getAnnotation(DBSave.class).typeConverter().newInstance();
                                o = tc.convertSave(o);
                            }
                            set.put(name, o);
                        }
                        else {
                            unset.put(name, "");
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
                    Logger.getLogger(GeneralPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for(String name : saveFields.keySet()) {
                try {
                    Field f = saveFields.get(name);
                    f.setAccessible(true);
                    Object o = f.get(object);
                    if(o != null) {
                        if (Enum.class.isAssignableFrom(o.getClass())) {
                            o = o.toString();
                        } else if (!f.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                            TypeConverter tc = f.getAnnotation(DBSave.class).typeConverter().newInstance();
                            o = tc.convertSave(o);
                        }
                        set.put(name, o);
                    }
                    else {
                        unset.put(name, "");
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InstantiationException ex) {
                    Logger.getLogger(GeneralPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(index != null) {
                DBObject query = new BasicDBObject();
                query.put("$set", set);
                query.put("$unset", set);
                return (ObjectId)dbcoll.update(index, new BasicDBObject("$set", set)).getField("_id");
            }
            else {
                dbcoll.insert(set);
                return (ObjectId) dbcoll.findOne(index).get("_id");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static <T> T load(DBObject dbo, Class<T> c) {
        try {
            T t = c.newInstance();
            HashMap<String, Method> loadMethods = getLoadMethods(c);
            HashMap<String, Field> loadFields = getLoadFields(c);
            for (String key : dbo.keySet()) {
                Method m = loadMethods.get(key);
                if(m != null) {
                    m.setAccessible(true);
                    Object o = dbo.get(key);
                    if (m.getParameterTypes()[0].isEnum() && o instanceof String) {
                        m.invoke(t, Enum.valueOf((Class<Enum>) m.getParameterTypes()[0], (String) o));
                    } else if (!m.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                        TypeConverter tc = m.getAnnotation(DBLoad.class).typeConverter().newInstance();
                        m.invoke(t, tc.convertLoad(o));
                    } else {
                        m.invoke(t, o);
                    }
                }
                else {
                    Field f = loadFields.get(key);
                    if(f != null) {
                        f.setAccessible(true);
                        Object o = dbo.get(key);
                        if (f.getType().isEnum() && o instanceof String) {
                            f.set(t, Enum.valueOf((Class<Enum>) f.getType(), (String) o));
                        } else if (!f.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                            TypeConverter tc = f.getAnnotation(DBLoad.class).typeConverter().newInstance();
                            f.set(t, tc.convertLoad(o));
                        } else {
                            f.set(t, o);
                        }
                    }
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static HashMap<String, Method> getLoadMethods(Class c) {
        HashMap<String, Method> loadMethods = new HashMap<>();
        Class current = c;
        while (current != null && current.isAssignableFrom(c)) {
            for (Method m : current.getDeclaredMethods()) {
                DBLoad dbload = m.getAnnotation(DBLoad.class);
                if (dbload != null) {
                    loadMethods.put(dbload.fieldName(), m);
                }

            }
            current = current.getSuperclass();
        }
        return loadMethods;
    }
    
    public static HashMap<String, Method> getSaveMethods(Class c) {
        HashMap<String, Method> saveMethods = new HashMap<>();
        Class current = c;
        while (current != null && current.isAssignableFrom(c)) {
            for (Method m : current.getDeclaredMethods()) {
                DBSave dbload = m.getAnnotation(DBSave.class);
                if (dbload != null) {
                    saveMethods.put(dbload.fieldName(), m);
                }

            }
            current = current.getSuperclass();
        }
        return saveMethods;
    }
    
    public static HashMap<String, Field> getLoadFields(Class c) {
        HashMap<String, Field> loadFields = new HashMap<>();
        Class current = c;
        while (current != null && current.isAssignableFrom(c)) {
            for (Field f : current.getDeclaredFields()) {
                DBLoad dbload = f.getAnnotation(DBLoad.class);
                if (dbload != null) {
                    loadFields.put(dbload.fieldName(), f);
                }

            }
            current = current.getSuperclass();
        }
        return loadFields;
    }
    
    public static HashMap<String, Field> getSaveFields(Class c) {
        HashMap<String, Field> saveFields = new HashMap<>();
        Class current = c;
        while (current != null && current.isAssignableFrom(c)) {
            for (Field f : current.getDeclaredFields()) {
                DBSave dbload = f.getAnnotation(DBSave.class);
                if (dbload != null) {
                    saveFields.put(dbload.fieldName(), f);
                }
            }
            current = current.getSuperclass();
        }
        return saveFields;
    }
}
