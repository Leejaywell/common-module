package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.Json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-12-10 21:13:00
 */
@SuppressWarnings("unchecked")
public class FlatPropertyUtils {
    public static HashMap structurize(Map properties) {
        HashMap structuralProperties = new HashMap();
        for (Object key : properties.keySet()) {
            String propertyName = (String) key;
            Object propertyValue = properties.get(key);

            HashMap innerProperties = structuralProperties;
            String[] propertyParts = propertyName.split("\\.");
            for (int i = 0; i < propertyParts.length - 1; i++) {
                if (innerProperties.get(propertyParts[i]) == null) {
                    innerProperties.put(propertyParts[i], new HashMap());
                }
                innerProperties = (HashMap) innerProperties.get(propertyParts[i]);
            }

            if (propertyValue instanceof List) {
                List list = (List) propertyValue;
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Map) {
                        list.set(i, structurize((Map)item));
                    }
                }
            } else if (propertyValue instanceof Map) {
                Map map = (Map) propertyValue;
                for (Object keyObject : map.keySet()) {
                    Object value = map.get(keyObject);
                    if (value instanceof Map) {
                        map.put(keyObject, structurize((Map) map.get(keyObject)));
                    }
                }
            }
            innerProperties.put(propertyParts[propertyParts.length - 1], propertyValue);
        }
        return structuralProperties;
    }

    public static Object toObject(Map properties, Type type) {
        return Json.getDefault().toJavaObject(structurize(properties), type);
    }
}
