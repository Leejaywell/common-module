package pers.lee.common.lang.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.joda.time.LocalDateTime;

/**
 * Created by Passyt on 2018/11/12.
 */
public class LocalDateTimeHelper {

    private static LocalDateTimeHelper INSTANCE = new LocalDateTimeHelper();
    private LoadingCache<Object, Object> cache = CacheBuilder.from("maximumSize=1000").build(new CacheLoader<Object, Object>() {
        @Override
        public Object load(Object key) throws Exception {
            System.out.println(key.getClass() + "=" + key);
            if (key instanceof String) {
                return LocalDateTime.parse(String.class.cast(key));
            } else {
                return key.toString();
            }
        }
    });

    private LocalDateTimeHelper() {
    }

    public LocalDateTime parse(String date) {
        return (LocalDateTime) cache.getUnchecked(date);
    }

    public String toString(LocalDateTime date) {
        return (String) cache.getUnchecked(date);
    }

    public static LocalDateTimeHelper getInstance() {
        return INSTANCE;
    }
}
