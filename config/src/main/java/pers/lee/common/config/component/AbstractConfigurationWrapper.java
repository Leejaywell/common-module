package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import pers.lee.common.lang.properties.SortStringMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * AbstractConfigurationWrapper
 *
 * @author Drizzt Yang
 */
public class AbstractConfigurationWrapper implements Configuration {
    private Configuration configuration;
    @Override
    public String getName() {
        return configuration.getName();
    }

    @Override
    public Configuration subset(String prefix) {
        return configuration.subset(prefix);
    }

    @Override
    public boolean isEmpty() {
        return configuration.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return configuration.containsKey(key);
    }

    @Override
    public void setProperty(String key, String value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, Boolean value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, Integer value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, Long value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, BigDecimal value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, BigInteger value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, List<String> value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, Set<String> value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, Map<String, String> value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void setProperty(String key, Properties value) {
        configuration.setProperty(key, value);
    }

    @Override
    public void clearProperty(String key) {
        configuration.clearProperty(key);
    }

    @Override
    public String getProperty(String key) {
        return configuration.getProperty(key);
    }

    @Override
    public Set<String> getKeys(String prefix) {
        return configuration.getKeys(prefix);
    }

    @Override
    public Set<String> getKeys() {
        return configuration.getKeys();
    }

    @Override
    public Boolean getBoolean(String key) {
        return configuration.getBoolean(key);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return configuration.getBoolean(key, defaultValue);
    }

    @Override
    public Integer getInteger(String key) {
        return configuration.getInteger(key);
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return configuration.getInteger(key, defaultValue);
    }

    @Override
    public Long getLong(String key) {
        return configuration.getLong(key);
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return configuration.getLong(key, defaultValue);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return configuration.getBigDecimal(key);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return configuration.getBigDecimal(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return configuration.getBigInteger(key);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return configuration.getBigInteger(key, defaultValue);
    }

    @Override
    public String getString(String key) {
        return configuration.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return configuration.getString(key, defaultValue);
    }

    @Override
    public List<String> getList(String key) {
        return configuration.getList(key);
    }

    @Override
    public Set<String> getSet(String key) {
        return configuration.getSet(key);
    }

    @Override
    public Properties getProperties(String key) {
        return configuration.getProperties(key);
    }

    @Override
    public Map<String, String> getMap(String key) {
        return configuration.getMap(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        return configuration.getObject(key, type);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> type) {
        return configuration.getList(key, type);
    }

    @Override
    public <T> Set<T> getSet(String key, Class<T> type) {
        return configuration.getSet(key, type);
    }

    @Override
    public <T> Map<String, T> getMap(String key, Class<T> type) {
        return configuration.getMap(key, type);
    }

    @Override
    public void init() {
        configuration.init();
    }

    @Override
    public void destroy() {
        configuration.destroy();
    }

    @Override
    public void addListener(ConfigurationListener listener) {
        configuration.addListener(listener);
    }

    @Override
    public Properties toProperties() {
        return configuration.toProperties();
    }

    @Override
    public SortStringMap toStringMap() {
        return configuration.toStringMap();
    }

    @Override
    public boolean available() {
        return configuration.available();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
