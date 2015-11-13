/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.function;

import com.spleefleague.core.player.SLPlayer;

/**
 *
 * @author Jonas
 * @param <V>
 */
public interface Dynamic<V> {
    public V get(SLPlayer slp);
    
    public static <V> Dynamic<V> getConstant(V constant) {
        return (SLPlayer slp) -> constant;
    }
    
    public static <V> DynamicDefault<V> getDynamicDefault(Dynamic<V> dynamicValue, V defaultValue) {
        return getDynamicDefault(dynamicValue, defaultValue, null);
    }
    
    public static <V> DynamicDefault<V> getDynamicDefault(Dynamic<V> dynamicValue, V defaultValue, V nullValue) {
        return new DynamicDefault<V>(defaultValue, nullValue) {
            @Override
            public V getValue(SLPlayer slp) {
                return dynamicValue.get(slp);
            }
        };
    }
}
