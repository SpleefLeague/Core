/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.collections;

/**
 *
 * @author Jonas
 * @param <K>
 * @param <V>
 */
public class ReversibleHashMap<K,V> extends java.util.HashMap<K,V> {
    
    private final java.util.HashMap<V,K> reverseMap;

    public ReversibleHashMap() {
        super();
        reverseMap = new java.util.HashMap<>();
    }

    @Override
    public V put(K k, V v) {
        reverseMap.put(v, k);
        return super.put(k,v);
    }

    public K reverseGet(V v) {
        return reverseMap.get(v);
    }
}