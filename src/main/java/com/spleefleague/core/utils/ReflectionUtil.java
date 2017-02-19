/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jonas
 */
public class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        Class<?>[] p = DataType.convertToPrimitive(parameterTypes);
        for (Constructor<?> c : clazz.getConstructors()) {
            if (DataType.equalsArray(DataType.convertToPrimitive(c.getParameterTypes()), p)) {
                return c;
            }
        }
        return null;
    }

    public static Object newInstance(Class<?> clazz, Object... args) throws Exception {
        return getConstructor(clazz, DataType.convertToPrimitive(args)).newInstance(args);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        Class<?>[] p = DataType.convertToPrimitive(parameterTypes);
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name) && DataType.equalsArray(DataType.convertToPrimitive(m.getParameterTypes()), p)) {
                return m;
            }
        }
        return null;
    }

    public static Object invokeMethod(String name, Object instance, Object... args) throws Exception {
        return getMethod(instance.getClass(), name, DataType.convertToPrimitive(args)).invoke(instance, args);
    }

    public static Object invokeMethod(Class<?> clazz, String name, Object instance, Object... args) throws Exception {
        return getMethod(clazz, name, DataType.convertToPrimitive(args)).invoke(instance, args);
    }

    public static Field getField(Class<?> clazz, String name) throws Exception {
        Field f = clazz.getField(name);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        f.setAccessible(true);
        return f;
    }

    public static Field getDeclaredField(Class<?> clazz, String name) throws Exception {
        Field f = clazz.getDeclaredField(name);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        f.setAccessible(true);
        return f;
    }

    public static Object getValue(Object instance, String fieldName) throws Exception {
        return getField(instance.getClass(), fieldName).get(instance);
    }

    public static Object getValue(Class<?> clazz, Object instance, String fieldName) throws Exception {
        return getField(clazz, fieldName).get(instance);
    }

    public static Object getDeclaredValue(Object instance, String fieldName) throws Exception {
        return getDeclaredField(instance.getClass(), fieldName).get(instance);
    }

    public static Object getDeclaredValue(Class<?> clazz, Object instance, String fieldName) throws Exception {
        return getDeclaredField(clazz, fieldName).get(instance);
    }

    public static void setValue(Object instance, String fieldName, Object fieldValue) throws Exception {
        Field f = getField(instance.getClass(), fieldName);
        f.set(instance, fieldValue);
    }

    public static void setValue(Object instance, FieldPair pair) throws Exception {
        setValue(instance, pair.getName(), pair.getValue());
    }

    public static void setValue(Class<?> clazz, Object instance, String fieldName, Object fieldValue) throws Exception {
        Field f = getField(clazz, fieldName);
        f.set(instance, fieldValue);
    }

    public static void setValue(Class<?> clazz, Object instance, FieldPair pair) throws Exception {
        setValue(clazz, instance, pair.getName(), pair.getValue());
    }

    public static void setValues(Object instance, FieldPair... pairs) throws Exception {
        for (FieldPair pair : pairs) {
            setValue(instance, pair);
        }
    }

    public static void setValues(Class<?> clazz, Object instance, FieldPair... pairs) throws Exception {
        for (FieldPair pair : pairs) {
            setValue(clazz, instance, pair);
        }
    }

    public static void setDeclaredValue(Object instance, String fieldName, Object fieldValue) throws Exception {
        Field f = getDeclaredField(instance.getClass(), fieldName);
        f.set(instance, fieldValue);
    }

    public static void setDeclaredValue(Object instance, FieldPair pair) throws Exception {
        setDeclaredValue(instance, pair.getName(), pair.getValue());
    }

    public static void setDeclaredValue(Class<?> clazz, Object instance, String fieldName, Object fieldValue) throws Exception {
        Field f = getDeclaredField(clazz, fieldName);
        f.set(instance, fieldValue);
    }

    public static void setDeclaredValue(Class<?> clazz, Object instance, FieldPair pair) throws Exception {
        setDeclaredValue(clazz, instance, pair.getName(), pair.getValue());
    }

    public static void setDeclaredValues(Object instance, FieldPair... pairs) throws Exception {
        for (FieldPair pair : pairs) {
            setDeclaredValue(instance, pair);
        }
    }

    public static void setDeclaredValues(Class<?> clazz, Object instance, FieldPair... pairs) throws Exception {
        for (FieldPair pair : pairs) {
            setDeclaredValue(clazz, instance, pair);
        }
    }

    /**
     * This class is part of the ReflectionHandler and follows the same usage
     * conditions
     *
     * @author DarkBlade12
     */
    public enum DataType {
        BYTE(byte.class, Byte.class),
        SHORT(short.class, Short.class),
        INTEGER(int.class, Integer.class),
        LONG(long.class, Long.class),
        CHARACTER(char.class, Character.class),
        FLOAT(float.class, Float.class),
        DOUBLE(double.class, Double.class),
        BOOLEAN(boolean.class, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<>();
        private final Class<?> primitive;
        private final Class<?> reference;

        static {
            for (DataType t : values()) {
                CLASS_MAP.put(t.primitive, t);
                CLASS_MAP.put(t.reference, t);
            }
        }

        private DataType(Class<?> primitive, Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        public Class<?> getPrimitive() {
            return this.primitive;
        }

        public Class<?> getReference() {
            return this.reference;
        }

        public static DataType fromClass(Class<?> c) {
            return CLASS_MAP.get(c);
        }

        public static Class<?> getPrimitive(Class<?> c) {
            DataType t = fromClass(c);
            return t == null ? c : t.getPrimitive();
        }

        public static Class<?> getReference(Class<?> c) {
            DataType t = fromClass(c);
            return t == null ? c : t.getReference();
        }

        public static Class<?>[] convertToPrimitive(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class<?>[] types = new Class<?>[length];
            for (int i = 0; i < length; i++) {
                types[i] = getPrimitive(classes[i]);
            }
            return types;
        }

        public static Class<?>[] convertToPrimitive(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class<?>[] types = new Class<?>[length];
            for (int i = 0; i < length; i++) {
                types[i] = getPrimitive(objects[i].getClass());
            }
            return types;
        }

        public static boolean equalsArray(Class<?>[] a1, Class<?>[] a2) {
            if (a1 == null || a2 == null || a1.length != a2.length) {
                return false;
            }
            for (int i = 0; i < a1.length; i++) {
                if (!a1[i].equals(a2[i]) && !a1[i].isAssignableFrom(a2[i])) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * This class is part of the ReflectionHandler and follows the same usage
     * conditions
     *
     * @author DarkBlade12
     */
    public final class FieldPair {

        private final String name;
        private final Object value;

        public FieldPair(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public Object getValue() {
            return this.value;
        }
    }
}
