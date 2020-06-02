package pers.lee.common.lang.properties;

import java.util.LinkedHashMap;

/**
 * SummaryMap
 *
 * @author Drizzt Yang
 */
public class SummaryMap extends LinkedHashMap<Object, Integer> {
    public synchronized void addKeyCount(String key) {
        if(containsKey(key)) {
            put(key, get(key) + 1);
        } else {
            put(key, 1);
        }
    }
}
