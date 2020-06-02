package pers.lee.common.rpc.ci.config;

import pers.lee.common.config.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * ConfigService
 *
 * @author Drizzt Yang
 */
public class ConfigService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    private ApplicationConfiguration applicationConfiguration;
    private ConfigFilterProperties configFilter;

    public ConfigService() {
        configFilter = new ConfigFilterProperties();
    }

    public Properties getAll() {
        Properties properties = applicationConfiguration.toProperties();
        Properties filterProperties = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (!configFilter.isVisible(key)) {
                LOGGER.debug("config " + key + " is not allowed to display");
                continue;
            }
            filterProperties.setProperty(key, entry.getValue().toString());
        }
        return filterProperties;
    }

    public String getProperty(String key) {
        if (!configFilter.isVisible(key)) {
            LOGGER.debug("config " + key + " is not allowed to display");
            return null;
        }
        return applicationConfiguration.getString(key);
    }

    public void setProperty(String key, String value) {
        if (!configFilter.isVariable(key)) {
            LOGGER.debug("config " + key + " is not allowed to change");
            return;
        }
        if (value == null) {
            applicationConfiguration.clearProperty(key);
            return;
        }
        value = value.trim();
        if (value.equalsIgnoreCase("null")) {
            applicationConfiguration.clearProperty(key);
            return;
        }
        applicationConfiguration.setProperty(key, value);
    }

    public void setProperties(Map<String, String> properties) {
        if (properties == null) {
            return;
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey().trim();
            if (!configFilter.isVariable(key)) {
                LOGGER.debug("config " + key + " is not allowed to change");
                continue;
            }
            applicationConfiguration.setProperty(key, entry.getValue().trim());
        }
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        for (String key : applicationConfiguration.getReadOnlyKeys()) {
            if (configFilter.getConfigurationVisibility(key).equals(ConfigFilter.VARIABLE)) {
                configFilter.addFilter(key, ConfigFilter.READ_ONLY);
            }
        }
    }

    static ConfigService configService = new ConfigService();

    public static ConfigService get() {
        configService.setApplicationConfiguration(ApplicationConfiguration.get());
        return configService;
    }
}
