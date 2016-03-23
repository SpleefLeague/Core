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
public abstract class DynamicDefault<V> implements Dynamic<V> {

    private final V defaultValue, nullValue;

    protected DynamicDefault(V defaultValue) {
        this(defaultValue, null);
    }

    protected DynamicDefault(V defaultValue, V nullValue) {
        this.defaultValue = defaultValue;
        this.nullValue = nullValue;
    }

    @Override
    public V get(SLPlayer slp) {
        try {
            V value = slp == null ? defaultValue : getValue(slp);
            return value == null ? nullValue : value;
        } catch (NullPointerException npe) {
            return nullValue;
        }
    }

    protected abstract V getValue(SLPlayer slp);
}
