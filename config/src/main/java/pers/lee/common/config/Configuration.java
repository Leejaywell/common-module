package pers.lee.common.config;

import pers.lee.common.lang.properties.SortStringMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface Configuration {
    String getName();

    Configuration subset(String prefix);

    boolean isEmpty();

    boolean containsKey(String key);

    void setProperty(String key, String value);

    void setProperty(String key, Boolean value);

    void setProperty(String key, Integer value);

    void setProperty(String key, Long value);

    void setProperty(String key, BigDecimal value);

    void setProperty(String key, BigInteger value);

    void setProperty(String key, List<String> value);

    void setProperty(String key, Set<String> value);

    void setProperty(String key, Map<String, String> value);

    void setProperty(String key, Properties value);

    void clearProperty(String key);

    String getProperty(String key);

    Set<String> getKeys(String prefix);

    Set<String> getKeys();

    Boolean getBoolean(String key);

    Boolean getBoolean(String key, Boolean defaultValue);

    Integer getInteger(String key);

    Integer getInteger(String key, Integer defaultValue);

    Long getLong(String key);

    Long getLong(String key, Long defaultValue);

    BigDecimal getBigDecimal(String key);

    BigDecimal getBigDecimal(String key, BigDecimal defaultValue);

    BigInteger getBigInteger(String key);

    BigInteger getBigInteger(String key, BigInteger defaultValue);

    String getString(String key);

    String getString(String key, String defaultValue);

    List<String> getList(String key);

    Set<String> getSet(String key);

    Properties getProperties(String key);

    Map<String, String> getMap(String key);

    <T> T getObject(String key, Class<T> type);

    <T> List<T> getList(String key, Class<T> type);

    <T> Set<T> getSet(String key, Class<T> type);

    <T> Map<String, T> getMap(String key, Class<T> type);

    void init();

    void destroy();

    void addListener(ConfigurationListener listener);

    Properties toProperties();

    SortStringMap toStringMap();

    boolean available();
}
