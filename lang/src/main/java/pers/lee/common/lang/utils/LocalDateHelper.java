package pers.lee.common.lang.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.joda.time.LocalDate;

/**
 * Created by Passyt on 2018/11/11.
 */
public class LocalDateHelper {

    private static LocalDateHelper INSTANCE = new LocalDateHelper();
    private LoadingCache<Object, Object> cache = CacheBuilder.from("maximumSize=1000").build(new CacheLoader<Object, Object>() {
        @Override
        public Object load(Object key) throws Exception {
            System.out.println(key.getClass() + "=" + key);
            if (key instanceof String) {
                return LocalDate.parse(String.class.cast(key));
            } else {
                return key.toString();
            }
        }
    });

    private LocalDateHelper() {
    }

    public LocalDate parse(String date) {
        return (LocalDate) cache.getUnchecked(date);
    }

    public String toString(LocalDate date) {
        return (String) cache.getUnchecked(date);
    }

    public static LocalDateHelper getInstance() {
        return INSTANCE;
    }
}
