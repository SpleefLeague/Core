package com.spleefleague.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class UtilReflect {
    
    /**
     * Returns list of fields (with their types) of the class of given object and of all superclasses.
     * @param object
     * @return human-understandable list of fields with their types.
     */
    public static List<String> listFieldsRecursively(Object object) {
        List<String> list = new ArrayList<>();
        Class clazz = object.getClass();
        while(clazz != Object.class) {
            for(Field f : clazz.getDeclaredFields()) {
                list.add(String.format("%s (%s)", f.getName(), f.getType().getSimpleName()));
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }
    
    /**
     * Returns list of fields (with their types) of the class of given object and.
     * @param object
     * @return human-understandable list of fields with their types.
     */
    public static List<String> listFields(Object object) {
        List<String> list = new ArrayList<>();
        Class clazz = object.getClass();
        for(Field f : clazz.getDeclaredFields())
            list.add(String.format("%s (%s)", f.getName(), f.getType().getSimpleName()));
        return list;
    }
    
    /**
     * Returns list of methods (with their arguments) of the class of given object and of all superclasses.
     * @param object
     * @return human-understandable list of methods with their arguments.
     */
    public static List<String> listMethodsRecursively(Object object) {
        List<String> list = new ArrayList<>();
        Class clazz = object.getClass();
        while(clazz != Object.class) {
            for(Method m : clazz.getDeclaredMethods()) {
                if(m.getName().contains("$"))
                    continue;
                StringBuilder args = new StringBuilder();
                for(Class cls : m.getParameterTypes())
                    args.append(cls.getSimpleName()).append(" ");
                list.add(String.format("%s (%s)", m.getName(), args.toString().trim()));
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }
    
    /**
     * Returns list of methods (with their arguments) of the class of given object.
     * @param object
     * @return human-understandable list of methods with their arguments.
     */
    public static List<String> listMethods(Object object) {
        List<String> list = new ArrayList<>();
        Class clazz = object.getClass();
        for(Method m : clazz.getDeclaredMethods()) {
            if(m.getName().contains("$"))
                continue;
            StringBuilder args = new StringBuilder();
            for(Class cls : m.getParameterTypes())
                args.append(cls.getSimpleName()).append(" ");
            list.add(String.format("%s (%s)", m.getName(), args.toString().trim()));
        }
        return list;
    }

    /**
     * Returns value of field of the given name of target object's class or of one of it's superclasses.
     * @param object target object.
     * @param fieldName name of the field.
     * @return value of the field.
     * @throws Exception 
     */
    public static Object getFieldRecursively(Object object, String fieldName) throws Exception {
        Class clazz = object.getClass();
        boolean ended = false;
        do {
            for(Field f : clazz.getDeclaredFields())
                if(f.getName().equalsIgnoreCase(fieldName)) {
                    ended = true;
                    f.setAccessible(true);
                    try {
                        return f.get(object);
                    }finally {
                        f.setAccessible(false);
                    }
                }
            clazz = clazz.getSuperclass();
        }while(clazz != Object.class && !ended);
        throw new NullPointerException("There is no field with given name");
    }
    
    /**
     * Returns value of field of the given name of target object's class.
     * @param object target object.
     * @param fieldName name of the field.
     * @return value of the field.
     * @throws Exception 
     */
    public static Object getField(Object object, String fieldName) throws Exception {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        try {
            return f.get(object);
        }finally {
            f.setAccessible(false);
        }
    }
    
    /**
     * Sets value of field of the given name of target object's class or of one of it's superclasses.
     * Be aware that string-value which is convertable to number WILL BE converted to number.
     * @param object target object.
     * @param fieldName name of the field.
     * @param value new value of the field.
     * @throws Exception 
     */
    public static void setFieldRecursively(Object object, String fieldName, Object value) throws Exception {
        Class clazz = object.getClass();
        boolean ended = false;
        do {
            for(Field f : clazz.getDeclaredFields())
                if(f.getName().equalsIgnoreCase(fieldName)) {
                    ended = true;
                    f.setAccessible(true);
                    if(value instanceof String)
                        if(NumberUtils.isNumber((String) value))
                            value = NumberUtils.createNumber((String) value);
                        else {
                            try {
                                value = f.getType().getConstructor(String.class).newInstance((String) value);
                            }catch(NoSuchMethodException | SecurityException | InstantiationException |
                                    IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {}
                        }
                    f.set(object, value);
                    f.setAccessible(false);
                    return;
                }
            clazz = clazz.getSuperclass();
        }while(clazz != Object.class && !ended);
        throw new NullPointerException("There is no field with given name");
    }
    
    /**
     * Sets value of field of the given name of target object's class.
     * Be aware that string-value which is convertable to number WILL BE converted to number.
     * @param object target object.
     * @param fieldName name of the field.
     * @param value new value of the field.
     * @throws Exception 
     */
    public static void setField(Object object, String fieldName, Object value) throws Exception {
        Field f = object.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        if(value instanceof String)
            if(NumberUtils.isNumber((String) value))
                value = NumberUtils.createNumber((String) value);
            else {
                try {
                    value = f.getType().getConstructor(String.class).newInstance((String) value);
                }catch(NoSuchMethodException | SecurityException | InstantiationException |
                        IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {}
            }
        f.set(object, value);
        f.setAccessible(false);
    }
    
    /**
     * Invokes method with given name and arguments of target object's class or one of it's superclasses.
     * @param object target object.
     * @param methodName name of the method.
     * @param args arguments.
     * @throws Exception 
     */
    public static void invokeMethodRecursively(Object object, String methodName, Object... args) throws Exception {
        invokeMethodAndGetRecursively(object, methodName, args);
    }
    
    /**
     * Invokes method with given name and arguments of target object's class.
     * @param object target object.
     * @param methodName name of the method.
     * @param args arguments.
     * @throws Exception 
     */
    public static void invokeMethod(Object object, String methodName, Object... args) throws Exception {
        invokeMethodAndGet(object, methodName, args);
    }
    
    /**
     * Invokes method with given name and arguments of target object's class or one of it's superclasses.
     * Be aware of that all boolean/number-convertable arguments WILL BE converted to boolean/number.
     * @param object target object.
     * @param methodName name of the method.
     * @param strings arguments.
     * @throws Exception 
     */
    public static void invokeMethodWithStringArgsRecursively(Object object, String methodName, String... strings) throws Exception {
        invokeMethodAndGetWithStringArgsRecursively(object, methodName, strings);
    }
    
    /**
     * Invokes method with given name and arguments of target object's class.
     * Be aware of that all boolean/number-convertable arguments WILL BE converted to boolean/number.
     * @param object target object.
     * @param methodName name of the method.
     * @param strings arguments.
     * @throws Exception 
     */
    public static void invokeMethodWithStringArgs(Object object, String methodName, String... strings) throws Exception {
        invokeMethodAndGetWithStringArgs(object, methodName, strings);
    }
    
    /**
     * Invokes and returns result of method with given name and arguments of target object's class or one of it's superclasses.
     * @param object target object.
     * @param methodName name of the method.
     * @param args arguments.
     * @return result of method invokation.
     * @throws Exception 
     */
    public static Object invokeMethodAndGetRecursively(Object object, String methodName, Object... args) throws Exception {
        Class clazz = object.getClass();
        boolean ended = false;
        do {
            for(Method m : clazz.getDeclaredMethods())
                if(m.getName().equalsIgnoreCase(methodName)) {
                    ended = true;
                    m.setAccessible(true);
                    try {
                        return m.invoke(object, args);
                    }finally {
                        m.setAccessible(false);
                    }
                }
            clazz = clazz.getSuperclass();
        }while(clazz != Object.class && !ended);
        throw new NullPointerException("There is no method with given name");
    }
    
    /**
     * Invokes and returns result of method with given name and arguments of target object's class.
     * @param object target object.
     * @param methodName name of the method.
     * @param args arguments.
     * @return result of method invokation.
     * @throws Exception 
     */
    public static Object invokeMethodAndGet(Object object, String methodName, Object... args) throws Exception {
        Class[] params = new Class[args.length];
        for(int i = 0; i < args.length; ++i)
            params[i] = args[i].getClass();
        Method m = object.getClass().getDeclaredMethod(methodName, params);
        m.setAccessible(true);
        try {
            return m.invoke(object, args);
        }finally {
            m.setAccessible(false);
        }
    }
    
    /**
     * Invokes and returns result of method with given name and arguments of target object's class or one of it's superclasses.
     * Be aware of that all boolean/number-convertable arguments WILL BE converted to boolean/number.
     * @param object target object.
     * @param methodName name of the method.
     * @param strings arguments.
     * @return result of method invokation.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    public static Object invokeMethodAndGetWithStringArgsRecursively(Object object, String methodName, String... strings) throws Exception {
        Class clazz = object.getClass();
        boolean ended = false;
        do {
            for(Method m : clazz.getDeclaredMethods())
                if(m.getName().equalsIgnoreCase(methodName)) {
                    ended = true;
                    Class[] types = m.getParameterTypes();
                    Object[] args = new Object[strings.length];
                    for(int i = 0; i < args.length; ++i) {
                        String s = strings[i];
                        if(s.equalsIgnoreCase("true"))
                            args[i] = true;
                        else if(s.equalsIgnoreCase("false"))
                            args[i] = false;
                        else if(NumberUtils.isNumber(s))
                            args[i] = NumberUtils.createNumber(s);
                        else {
                            try {
                                args[i] = types[i].getConstructor(String.class).newInstance(s);
                            }catch(Exception ex) {
                                args[i] = s;
                            }
                        }
                    }
                    m.setAccessible(true);
                    try {
                        return m.invoke(object, args);
                    }finally {
                        m.setAccessible(false);
                    }
                }
            clazz = clazz.getSuperclass();
        }while(clazz != Object.class && !ended);
        throw new NullPointerException("There is no method with given name");
    }
    
    /**
     * Invokes and returns result of method with given name and arguments of target object's class.
     * Be aware of that all boolean/number-convertable arguments WILL BE converted to boolean/number.
     * @param object target object.
     * @param methodName name of the method.
     * @param strings arguments.
     * @return result of method invokation.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    public static Object invokeMethodAndGetWithStringArgs(Object object, String methodName, String... strings) throws Exception {
        Class clazz = object.getClass();
        for(Method m : clazz.getDeclaredMethods())
            if(m.getName().equalsIgnoreCase(methodName)) {
                Class[] types = m.getParameterTypes();
                Object[] args = new Object[strings.length];
                for(int i = 0; i < args.length; ++i) {
                    String s = strings[i];
                    if(s.equalsIgnoreCase("true"))
                        args[i] = true;
                    else if(s.equalsIgnoreCase("false"))
                        args[i] = false;
                    else if(NumberUtils.isNumber(s))
                        args[i] = NumberUtils.createNumber(s);
                    else {
                        try {
                            args[i] = types[i].getConstructor(String.class).newInstance(s);
                        }catch(Exception ex) {
                            args[i] = s;
                        }
                    }
                }
                m.setAccessible(true);
                try {
                    return m.invoke(object, args);
                }finally {
                    m.setAccessible(false);
                }
            }
        throw new NullPointerException("There is no method with given name");
    }
    
}
