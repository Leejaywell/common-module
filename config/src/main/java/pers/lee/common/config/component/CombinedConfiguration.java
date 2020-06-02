package pers.lee.common.config.component;


import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import pers.lee.common.lang.properties.SortStringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CombinedConfiguration
 *
 * @author Drizzt Yang
 */
public class CombinedConfiguration extends ReloadableConfiguration implements Configuration {
    protected List<Configuration> configurations = new ArrayList<Configuration>();
    protected Configuration updateConfiguration;

    public CombinedConfiguration() {
    }

    public CombinedConfiguration(List<Configuration> configurations) {
        this.configurations = configurations;
        updateConfiguration = configurations.get(configurations.size() - 1);
    }

    @Override
    public void init() {
        for (Configuration configuration : configurations) {
            configuration.init();
            configuration.addListener(new BroadcastListener(this));
        }
        this.stringMap = new SortStringMap();
        this.stringMap.putAll(reload());
        this.fireInit();
    }

    public void destroy() {
        for (Configuration configuration : configurations) {
            try {
                configuration.destroy();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void setProperty(String key, String value) {
        if(updateConfiguration != null) {
            updateConfiguration.setProperty(key, value);
        }
        super.setProperty(key, value);
    }

    @Override
    public void clearProperty(String key) {
        if(updateConfiguration != null) {
            updateConfiguration.clearProperty(key);
        }
        super.clearProperty(key);
    }

    @Override
    protected long getCurrentTimestamp() {
        return 0;
    }

    @Override
    protected Map<String, String> reload() {
        SortStringMap stringMap = new SortStringMap();
        for (Configuration configuration : configurations) {
            for (String key : configuration.getKeys()) {
                stringMap.put(key, configuration.getProperty(key));
            }
        }
        return stringMap;
    }

    public static class BroadcastListener implements ConfigurationListener {
        private CombinedConfiguration combinedConfiguration;

        public BroadcastListener(CombinedConfiguration combinedConfiguration) {
            this.combinedConfiguration = combinedConfiguration;
        }

        @Override
        public void notifyInit(Configuration configuration) {
        }

        @Override
        public void notifyUpdate(Configuration configuration, String key) {
            combinedConfiguration.applyReload();
        }
    }
}
