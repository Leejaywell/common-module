package pers.lee.common.rpc.ci.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * ConfigFilterProperties
 *
 * @author Drizzt Yang
 */
public class ConfigFilterProperties implements ConfigFilter {
    private Properties properties;
    private Set<String> filterPrefix;

    public ConfigFilterProperties() {
        properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("default.config.filter.properties"));
        } catch (Exception ignored) {
        }

        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.filter.properties"));
        } catch (Exception ignored) {
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            entry.setValue(entry.getValue().toString().trim().toLowerCase());
        }

        filterPrefix = new HashSet<String>();
        for (Object key : properties.keySet()) {
            if(key.toString().endsWith(".")) {
                filterPrefix.add(key.toString());
            }
        }
    }

    public void addFilter(String key, String value) {
        properties.setProperty(key, value);
    }

    @Override
    public String getConfigurationVisibility(String key) {
        String property = properties.getProperty(key);
        if(property != null) {
            return property;
        }
        for (String prefix : filterPrefix) {
            if(key.startsWith(prefix)) {
                return properties.getProperty(prefix);
            }
        }
        return VARIABLE;
    }

    @Override
    public boolean isVisible(String key) {
        return !getConfigurationVisibility(key).equals(INVISIBLE);
    }

    @Override
    public boolean isVariable(String key) {
        return getConfigurationVisibility(key).equals(VARIABLE);
    }
}
