/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.io;

import com.mongodb.client.MongoCollection;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.spleefleague.core.io.EntityBuilder.IOClass.Input;
import com.spleefleague.core.io.EntityBuilder.IOClass.Output;
import com.spleefleague.core.utils.collections.MapUtil;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Jonas
 */
public class EntityBuilder {

    public static <T extends DBEntity & DBSaveable> void save(T object, MongoCollection<Document> dbcoll) {
        save(object, dbcoll, true);
    }
    
    public static <T extends DBEntity & DBSaveable> void save(T object, MongoCollection<Document> dbcoll, boolean override) {
        DBEntity dbe = (DBEntity)object;
        ObjectId _id = dbe.getObjectId();
        Document index = null;
        if(override && _id != null) {
            index = new Document("_id", _id);
        }
        Document dbo = serialize(object);
        validate(dbo);
        if(index != null) {
            dbcoll.updateOne(index, dbo);
        }
        else {
            dbo = (Document)dbo.get("$set");
            dbcoll.insertOne(dbo);
            _id = (ObjectId)dbcoll.find(dbo).first().get("_id");
            try {
                Field _idField = DBEntity.class.getDeclaredField("_id");
                _idField.setAccessible(true);
                _idField.set(dbe, _id);
            } catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void validate(Document dbo) {
        Document unset = (Document)dbo.get("$unset");
        if(unset != null) {
            if(unset.keySet().isEmpty()) {
                dbo.remove("$unset");
            }
        }
    }

    public static Document serialize(DBSaveable object) {
        try {
            HashMap<String, Output> outputs = getOutputs(object.getClass());
            Document set = new Document();
            Document unset = new Document();
            Document query = new Document();
            for(String name : outputs.keySet()) {
                Object o = outputs.get(name).get(object);
                if(o != null) {
                    set.put(name, o);
                }
                else {
                    unset.put(name, "");
                }
            }
            query.put("$set", set);
            query.put("$unset", unset);
            return query;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends DBEntity & DBLoadable> T load(Document dbo, Class<T> c) {
        return deserialize(dbo, c);
    }

    public static <T> T deserialize(Document dbo, Class<T> c) {
        try {
            T t = c.newInstance();
            Map<String, Input> inputs = getInputs(c);
            for(String name : inputs.keySet()) {
                Object o = dbo.get(name);
                if(o != null) {
                    Input i = inputs.get(name);
                    i.set(t, o);
                }
            }
            return t;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, Input> getInputs(Class c) {
        Map<String, Input> inputs = new HashMap<>();
        Class current = c;
        while(current != null && current.isAssignableFrom(c)) {
            for(Method m : current.getDeclaredMethods()) {
                DBLoad dbload = m.getAnnotation(DBLoad.class);
                if(dbload != null) {
                    inputs.put(dbload.fieldName(), new Input(dbload.priority(), m));
                }

            }
            for(Field f : current.getDeclaredFields()) {
                DBLoad dbload = f.getAnnotation(DBLoad.class);
                if(dbload != null) {
                    inputs.put(dbload.fieldName(), new Input(dbload.priority(), f));
                }
            }
            current = current.getSuperclass();
        }
        inputs = MapUtil.sortByValue(inputs);
        return inputs;
    }

    private static HashMap<String, Output> getOutputs(Class c) {
        HashMap<String, Output> outputs = new HashMap<>();
        Class current = c;
        while(current != null && current.isAssignableFrom(c)) {
            for(Method m : current.getDeclaredMethods()) {
                DBSave dbsave = m.getAnnotation(DBSave.class);
                if(dbsave != null) {
                    outputs.put(dbsave.fieldName(), new Output(dbsave.priority(), m));
                }

            }
            for(Field f : current.getDeclaredFields()) {
                DBSave dbsave = f.getAnnotation(DBSave.class);
                if(dbsave != null) {
                    outputs.put(dbsave.fieldName(), new Output(dbsave.priority(), f));
                }
            }
            current = current.getSuperclass();
        }
        outputs = MapUtil.sortByValue(outputs);
        return outputs;
    }

    public static abstract class IOClass implements Comparable<IOClass> {

        private final Method method;
        private final Field field;
        private final int priority;

        public IOClass(int priority, Field field) {
            this.field = field;
            this.field.setAccessible(true);
            this.priority = priority;
            this.method = null;
        }

        public IOClass(int priority, Method method) {
            this.method = method;
            this.method.setAccessible(true);
            this.priority = priority;
            this.field = null;
        }

        public boolean isMethod() {
            return method != null;
        }

        public boolean isField() {
            return field != null;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public int compareTo(IOClass o) {
            if(priority > o.getPriority()) {
                return -1;
            }
            else if(priority < o.getPriority()) {
                return 1;
            }
            else {
                if(isField()) {
                    return (o.isField()) ? 0 : -1;
                }
                else {
                    return (o.isMethod()) ? 0 : 1;
                }
            }
        }

        public static class Output extends IOClass {

            public Output(int priority, Field field) {
                super(priority, field);
            }

            public Output(int priority, Method method) {
                super(priority, method);
            }

            public Object get(Object instance) {
                if(isField()) {
                    return getField(instance);
                }
                else {
                    return getMethod(instance);
                }
            }

            private Object getField(Object instance) {
                try {
                    Field f = super.field;
                    Object o = f.get(instance);
                    if(o == null) {
                        return null;
                    }
                    if(o.getClass().isEnum()) {
                        o = o.toString();
                    }
                    else if(o.getClass().isArray()) {
                        List list = new ArrayList<>();
                        Object[] array = (Object[])o;
                        if(!f.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                            TypeConverter tc = f.getAnnotation(DBSave.class).typeConverter().newInstance();
                            for(Object value : array) {
                                list.add(tc.convertSave(value));
                            }
                        }
                        else {
                            for(Object value : array) {
                                if(DBSaveable.class.isAssignableFrom(f.getType().getComponentType())) {
                                    value = serialize((DBSaveable)value).get("$set");
                                }
                                list.add(value);
                            }
                        }
                        o = list;
                    }
                    else if(!f.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                        TypeConverter tc = f.getAnnotation(DBSave.class).typeConverter().newInstance();
                        o = tc.convertSave(o);
                    }
                    else if(DBSaveable.class.isAssignableFrom(f.getType())) {
                        o = serialize((DBSaveable)o).get("$set");
                    }
                    return o;
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            private Object getMethod(Object instance) {
                try {
                    Method m = super.method;
                    Object o = m.invoke(instance);
                    if(o == null) {
                        return null;
                    }
                    if(m.getReturnType().isEnum()) {
                        o = o.toString();
                    }
                    else if(m.getReturnType().isArray()) {
                        List list = new ArrayList<>();
                        Object[] array = (Object[])o;
                        if(!m.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                            TypeConverter tc = m.getAnnotation(DBSave.class).typeConverter().newInstance();
                            for(Object value : array) {
                                list.add(tc.convertSave(value));
                            }
                        }
                        else {
                            for(Object value : array) {
                                if(DBSaveable.class.isAssignableFrom(m.getReturnType().getComponentType())) {
                                    value = serialize((DBSaveable)value).get("$set");
                                }
                                list.add(value);
                            }
                        }
                        o = list;
                    }
                    else if(!m.getAnnotation(DBSave.class).typeConverter().equals(TypeConverter.class)) {
                        TypeConverter tc = m.getAnnotation(DBSave.class).typeConverter().newInstance();
                        o = tc.convertSave(o);
                    }
                    else if(DBSaveable.class.isAssignableFrom(m.getReturnType())) {
                        o = serialize((DBSaveable)o).get("$set");
                    }
                    return o;
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

        public static class Input extends IOClass {

            public Input(int priority, Field field) {
                super(priority, field);
            }

            public Input(int priority, Method method) {
                super(priority, method);
            }

            public void set(Object instance, Object value) {
                if(isField()) {
                    setField(instance, value);
                }
                else {
                    setMethod(instance, value);
                }
            }

            private void setField(Object instance, Object value) {
                try {
                    Field f = super.field;
                    if(f.getType().isEnum() && value instanceof String) {
                        f.set(instance, Enum.valueOf((Class<Enum>)f.getType(), (String)value));
                    }
                    else if(f.getType().isArray() && value instanceof List) {
                        List list = (List)value;
                        Object[] array = new Object[list.size()];
                        for(int i = 0; i < list.size(); i++) {
                            Object o = list.get(i);
                            if(!f.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                                TypeConverter tc = f.getAnnotation(DBLoad.class).typeConverter().newInstance();
                                o = tc.convertLoad(o);
                            }
                            else {
                                if(o instanceof Document && DBLoadable.class.isAssignableFrom(f.getType().getComponentType())) {
                                    o = deserialize((Document)o, f.getType().getComponentType());
                                }
                            }
                            array[i] = o;
                        }
                        f.set(instance, createGenericArray(array, f.getType().getComponentType()));
                    }
                    else if(!f.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                        TypeConverter tc = f.getAnnotation(DBLoad.class).typeConverter().newInstance();
                        f.set(instance, tc.convertLoad(value));
                    }
                    else if(value instanceof Document && DBLoadable.class.isAssignableFrom(f.getType())) {
                        f.set(instance, deserialize((Document)value, f.getType()));
                    }
                    else {
                        f.set(instance, value);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            private void setMethod(Object instance, Object value) {
                try {
                    Method m = super.method;
                    if(m.getParameterTypes()[0].isEnum() && value instanceof String) {
                        m.invoke(instance, Enum.valueOf((Class<Enum>)m.getParameterTypes()[0], (String)value));
                    }
                    else if(m.getReturnType().isArray() && value instanceof List) {
                        List list = (List)value;
                        Object[] array = new Object[list.size()];
                        for(int i = 0; i < list.size(); i++) {
                            Object o = list.get(i);
                            if(!m.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                                TypeConverter tc = m.getAnnotation(DBLoad.class).typeConverter().newInstance();
                                o = tc.convertLoad(o);
                            }
                            else {
                                if(o instanceof Document && DBLoadable.class.isAssignableFrom(m.getReturnType().getComponentType())) {
                                    o = deserialize((Document)o, m.getReturnType().getComponentType());
                                }
                            }
                            array[i] = o;
                        }
                        m.invoke(instance, createGenericArray(array, m.getReturnType().getComponentType()));
                    }
                    else if(!m.getAnnotation(DBLoad.class).typeConverter().equals(TypeConverter.class)) {
                        TypeConverter tc = m.getAnnotation(DBLoad.class).typeConverter().newInstance();
                        m.invoke(instance, tc.convertLoad(value));
                    }
                    else if(value instanceof Document && DBLoadable.class.isAssignableFrom(m.getParameterTypes()[0])) {
                        m.invoke(instance, deserialize((Document)value, m.getParameterTypes()[0]));
                    }
                    else {
                        m.invoke(instance, value);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            private static <T> T[] createGenericArray(T[] values, Class<? extends T> cast) {
                T[] array = (T[])Array.newInstance(cast, values.length);
                for(int i = 0; i < values.length; i++) {
                    T value = values[i];
                    array[i] = cast.cast(value);
                }
                return array;
            }
        }
    }
}
