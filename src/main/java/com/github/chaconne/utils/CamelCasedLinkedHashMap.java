package com.github.chaconne.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @Description: CamelCasedLinkedHashMap
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class CamelCasedLinkedHashMap extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 7572318238502606513L;

    public CamelCasedLinkedHashMap() {
        super();
    }

    public CamelCasedLinkedHashMap(Map<String, Object> map) {
        super(map);
    }

    public CamelCasedLinkedHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(key != null ? key.toLowerCase() : null, value);
    }

    @Override
    public Object get(Object key) {
        return super.get(key != null ? convertKey(key.toString()) : null);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key != null ? convertKey(key.toString()) : null);
    }

    private String convertKey(String key) {
        StringBuilder str = new StringBuilder(key);
        for (int i = 1; i < str.length(); i++) {
            if (isUnderscoreRequired(str.charAt(i - 1), str.charAt(i))) {
                str.insert(i++, '_');
            }
        }
        return str.toString().toLowerCase();
    }

    private boolean isUnderscoreRequired(char before, char current) {
        return Character.isLowerCase(before) && Character.isUpperCase(current);
    }

}
