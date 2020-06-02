package pers.lee.common.lang.json.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author: Jay
 * @date: 2018/5/7
 */
public class CircularReferenceExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
