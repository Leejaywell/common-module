package pers.lee.common.lang.utils;

import pers.lee.common.lang.properties.SortStringMap;

import java.util.Map;

/**
 * PropertyUtils
 *
 * @author Drizzt Yang
 */
public class StringMapUtils {
    public static final String PLACEHOLDER_PREFIX = "${";

    public static final String PLACEHOLDER_SUFFIX = "}";

    public static String resolvePlaceholders(String text, Map<String, String> stringMap) {
        if (text == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer(text);

        int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
        while (startIndex != -1) {
            int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex + PLACEHOLDER_PREFIX.length());
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
                int nextIndex = endIndex + PLACEHOLDER_SUFFIX.length();
                String propVal = stringMap.get(placeholder);
                if (propVal != null) {
                    buf.replace(startIndex, endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
                    nextIndex = startIndex + propVal.length();
                } else {
                    //throw new IllegalStateException("Could not resolve placeholder '" + placeholder + "' in [" + text + "] as a property");
                }
                startIndex = buf.indexOf(PLACEHOLDER_PREFIX, nextIndex);
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    public static SortStringMap resolvePlaceholders(SortStringMap source, SortStringMap base) {
        SortStringMap resolvedMap = new SortStringMap();
        for (String key : source.keySet()) {
            String value = resolvePlaceholders(source.get(key), base);
            resolvedMap.put(key, value);
        }
        return resolvedMap;
    }

}
