package com.github.chaconne.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 
 * @Description: MapUtils
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public abstract class MapUtils {

    public static <K, V> V getOrCreate(Map<K, V> map, K key, Supplier<V> supplier) {
        if (map == null || supplier == null) {
            return null;
        }
        if (map instanceof ConcurrentMap) {
            return ((ConcurrentMap<K, V>) map).computeIfAbsent(key, k -> supplier.get());
        } else {
            synchronized (map) {
                return map.computeIfAbsent(key, k -> supplier.get());
            }
        }
    }

}
