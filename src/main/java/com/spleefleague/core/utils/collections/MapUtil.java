/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;

/**
 *
 * @author Jonas
 */
public class MapUtil {

    public static <K, V extends Comparable<? super V>, T extends Map<K, V>> T sortByValue(T map) {
        return sortByValue(map, false);
    }

    public static <K extends Comparable<? super K>, V, T extends Map<K, V>> T sortByKey(T map) {
        return sortByKey(map, false);
    }

    public static <K, V extends Comparable<? super V>, T extends Map<K, V>> T sortByValue(T map, final boolean descending) {
        return sort(map, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return ((o1.getValue()).compareTo(o2.getValue()) * (descending ? -1 : 1));
            }
        });
    }

    public static <K extends Comparable<? super K>, V, T extends Map<K, V>> T sortByKey(T map, final boolean descending) {
        return sort(map, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return ((o1.getKey()).compareTo(o2.getKey()) * (descending ? -1 : 1));
            }
        });
    }
    
    public static <V, T extends NavigableMap<Integer, V>> T compress(T map) {
        TreeMap<Integer, V> result = new TreeMap<>();
        Integer targetCursor = map.firstKey();
        Integer sourceCursor = map.firstKey() - 1;
        while(map.higherKey(sourceCursor) != null) {
            sourceCursor = map.higherKey(sourceCursor);
            result.put(targetCursor, map.get(sourceCursor));
            targetCursor++;
        }
        return (T) result;
    }

    public static <K, V, T extends Map<K, V>> T sort(T map, Comparator<Map.Entry<K, V>> c) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, c);
        Object result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            ((T) result).put(entry.getKey(), entry.getValue());
        }
        return (T) result;
    }
}
