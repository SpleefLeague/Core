/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

import java.util.UUID;

/**
 *
 * @author Jonas
 * @param <T>
 * @param <V>
 */
public abstract class TypeConverter<T, V> {
    public abstract V convertLoad(T t);
    public abstract T convertSave(V v);
    
    public static class UUIDStringConverter extends TypeConverter<String, UUID> {
        @Override
        public String convertSave(UUID t) {
            return t.toString();
        } 

        @Override
        public UUID convertLoad(String v) {
            return UUID.fromString(v);
        }
    }
}