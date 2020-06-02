package pers.lee.common.config;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.lee.common.config.component.*;
import pers.lee.common.lang.utils.StringMapUtils;

import java.io.File;
import java.util.*;

/**
 * ApplicationConfiguration, combine all the configurations, including servlet context, system, default.application.properties,
 * application.properties, ${application.key}/config.properties.
 * <p/>
 * You could pick any of them or all to combine. You can config it by init parameter "application.config" of the servlet
 * context.
 * <p/>
 * Only 1 of them would be updateable (add, update and remove). If database config exist, any changes would be updated to
 * the database. If file exist with no database, any changes would be updated to the file. If no file and database, the
 * changes would only in memory. It would be lost when the service was restarted.
 *
 * @author Drizzt Yang
 * @since 12-1-27 PM 8:29
 */
public class ApplicationConfiguration extends CombinedConfiguration implements Configuration {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    public static final String APPLICATION_DIRECTORY_PROPERTY = "application.directory";
    public static final String APPLICATION_PATH_PROPERTY = "application.path";
    public static final String APPLICATION_NAME_PROPERTY = "application.name";
    public static final String APPLICATION_ALIAS_PROPERTY = "application.alias";

    public static final String APPLICATION_KEY_PROPERTY = "application.key";
    public static final String APPLICATION_CONFIGS_PROPERTY = "application.configs";
    public static final String APPLICATION_CONFIG_DIRECTORY_PROPERTY = "application.config.directory";

    public static final String APPLICATION_CONFIGS_STATIC_PROPERTY = "application.configs.static";

    public static final String SYSTEM_CONFIG = "system";
    public static final String DEFAULT_CONFIG = "default";
    public static final String APPLICATION_CONFIG = "application";
    public static final String LOCAL_FILE_CONFIG = "file://${" + APPLICATION_CONFIG_DIRECTORY_PROPERTY + "}/local.properties";
    public static final String REMOTE_CONFIG = "remote";
    public static final List<String> DEFAULT_CONFIGS = Arrays.asList(SYSTEM_CONFIG, DEFAULT_CONFIG, APPLICATION_CONFIG, REMOTE_CONFIG);

    public static final Set<String> DEFAULT_CONFIGS_STATIC = Sets.newHashSet(LOCAL_FILE_CONFIG);

    public static final String CLASSPATH_CONFIG_PREFIX = "classpath:";
    public static final String FILE_PATH_CONFIG_PREFIX = "file:";

    private ApplicationConfiguration() {
    }

    @Override
    public void init() {
        addConfiguration(new SystemConfiguration());
        initConfigs();
    }

    public void init(Map<String, String> map) {
        addConfiguration(new SystemConfiguration());
        map.forEach((key, value) -> setProperty(key, value));
        initConfigs();
    }

    private void initConfigs() {
        initExternalConfigDirectory();

        List<String> configs = this.getList(APPLICATION_CONFIGS_PROPERTY);
        configs = configs == null ? scanExternalConfigProperties() : configs;

        Set<String> readOnlyConfigs = this.getSet(APPLICATION_CONFIGS_STATIC_PROPERTY);
        readOnlyConfigs = readOnlyConfigs == null ? DEFAULT_CONFIGS_STATIC : readOnlyConfigs;

        for (String config : configs) {
            Configuration configuration = null;
            config = config.trim();
            boolean isReadOnly = isReadOnlyConfig(config, readOnlyConfigs);
            if (config.equals(SYSTEM_CONFIG)) {
                configuration = new SystemConfiguration();
            } else if (config.equals(DEFAULT_CONFIG)) {
                configuration = new ClasspathResourceConfiguration("pers/lee/common/config/default.application.properties");
            } else if (config.equals(APPLICATION_CONFIG)) {
                configuration = new ClasspathResourceConfiguration("application.properties");
            } else if (config.startsWith(CLASSPATH_CONFIG_PREFIX)) {
                configuration = new ClasspathResourceConfiguration(config.substring(CLASSPATH_CONFIG_PREFIX.length()).trim());
            } else if (config.startsWith(FILE_PATH_CONFIG_PREFIX)) {
                configuration = initConfigFile(config.substring(FILE_PATH_CONFIG_PREFIX.length()).trim(), isReadOnly);
            } else if (config.equals(REMOTE_CONFIG)) {
                configuration = initRemoteConfig();
            }
            if (configuration == null) {
                continue;
            }
            if (isReadOnly) {
                configuration = new ReadOnlyConfiguration(configuration);
            }

            try {
                addConfiguration(configuration);
            } catch (Exception e) {
                LOGGER.warn("Ignore config [" + config + "]", e);
            }
        }

        for (Configuration configuration : configurations) {
            configuration.addListener(new BroadcastListener(this));
        }

        if (getApplicationPath() == null) {
            LOGGER.info("[{}] init config success...", getApplicationName());
        } else {
            LOGGER.info("[{}|{}] init config success...", getApplicationName(), getApplicationPath());
        }
        super.fireInit();
    }

    private FileConfiguration initConfigFile(String filePath, boolean isReadOnly) {
        String applicationConfigPath = StringMapUtils.resolvePlaceholders(filePath, stringMap);
        File file = new File(applicationConfigPath);
        if (!file.exists()) {
            return null;
        }

        try {
            return new FileConfiguration(FILE_PATH_CONFIG_PREFIX + filePath, file, !isReadOnly);
        } catch (IllegalStateException e) {
            LOGGER.warn("Ignore file configuration [" + applicationConfigPath + "]", e);
        }
        return null;
    }

    private RemoteConfiguration initRemoteConfig() {
        final String remoteUrl = this.getString("configkeeper.url");
        String remoteSourceKey = this.getString("configkeeper.source");
        remoteSourceKey = StringMapUtils.resolvePlaceholders(remoteSourceKey, stringMap);

        if (remoteUrl == null || remoteSourceKey == null) {
            LOGGER.warn("No configkeeper url or source");
            return null;
        }

        try {
            RemoteSource remoteSource = new RemoteSource(remoteUrl, remoteSourceKey);
            return new RemoteConfiguration("remote", remoteSource, true);
        } catch (Exception e) {
            LOGGER.warn("Ignore remote configuration [" + remoteUrl + "] [" + remoteSourceKey + "]", e);
        }
        return null;
    }

    private void initExternalConfigDirectory() {
        String configDir = null;
        String applicationAlias = this.getString(APPLICATION_ALIAS_PROPERTY);
        if (applicationAlias != null) {
            configDir = this.getProperty(applicationAlias + ".config");
        }
        String applicationKey = this.getString(APPLICATION_KEY_PROPERTY);
        if (configDir == null && applicationKey != null) {
            configDir = this.getProperty(applicationKey);
        }

        if (configDir == null) {
            LOGGER.warn("Ignore file configuration without application key [" + applicationKey + "] or alias ["
                    + applicationAlias + ".config] setup");
            return;
        } else if (configDir.endsWith("/")) {
            configDir = configDir.substring(0, configDir.length() - 1);
        }
        this.setProperty(APPLICATION_CONFIG_DIRECTORY_PROPERTY, configDir);
    }

    private List<String> scanExternalConfigProperties() {
        String configDirString = this.getProperty(APPLICATION_CONFIG_DIRECTORY_PROPERTY);
        if (configDirString == null) {
            return DEFAULT_CONFIGS;
        }

        File configDir = new File(configDirString);
        if (!configDir.exists()) {
            return DEFAULT_CONFIGS;
        }
        File[] files = configDir.listFiles();
        if (files == null) {
            return DEFAULT_CONFIGS;
        }

        List<String> configFiles = new ArrayList<String>();
        String localConfigFilePath = null;
        String baseConfigFilePath = null;
        for (File file : files) {
            if (!file.getName().endsWith(".properties")) {
                continue;
            }
            if (file.getName().equals("config.properties")) {
                baseConfigFilePath = "file://" + file.getPath();
            } else if (file.getName().equals("local.properties")) {
                localConfigFilePath = "file://" + file.getPath();
            } else if (file.getName().startsWith("config.")) {
                configFiles.add("file://" + file.getPath());
            }
        }

        List<String> configs = new ArrayList<String>();
        configs.add(SYSTEM_CONFIG);
        configs.add(DEFAULT_CONFIG);
        configs.add(APPLICATION_CONFIG);
        if (baseConfigFilePath != null) {
            configs.add(baseConfigFilePath);
        }

        Collections.sort(configFiles);
        configs.addAll(configFiles);

        configs.add(REMOTE_CONFIG);
        if (localConfigFilePath != null) {
            configs.add(localConfigFilePath);
        }

        return configs;
    }

    private boolean isReadOnlyConfig(String config, Set<String> readOnlyConfigs) {
        for (String readOnlyConfig : readOnlyConfigs) {
            if (config.equals(readOnlyConfig)) {
                return true;
            }
            String resolvedReadOnlyConfig = StringMapUtils.resolvePlaceholders(readOnlyConfig, stringMap);
            if (config.contains("\\")) {
                config = config.replaceAll("\\\\", "/");
            }
            if (resolvedReadOnlyConfig.contains("\\")) {
                resolvedReadOnlyConfig = resolvedReadOnlyConfig.replaceAll("\\\\", "/");
            }
            if (config.equals(resolvedReadOnlyConfig)) {
                return true;
            }
        }
        return false;
    }

    protected boolean addConfiguration(Configuration configuration) {
        if (!configuration.available()) {
            LOGGER.warn("Ignore unavailable configuration [" + configuration.getName() + "]");
            return false;
        }
        configuration.init();

        configurations.add(configuration);
        if (!(configuration instanceof ReadOnlyConfiguration)) {
            updateConfiguration = configuration;
        }
        this.stringMap = reload();
        return true;
    }

    @Override
    protected Map<String, String> reload() {
        Map<String, String> stringMap = super.reload();
        for (String key : stringMap.keySet()) {
            String value = StringMapUtils.resolvePlaceholders(stringMap.get(key), stringMap);
            stringMap.put(key, value);
        }
        return stringMap;
    }

    public void destroy() {
        super.destroy();
    }

    protected void fireChanges(String key) {
        for (ConfigurationListener listener : configurationListeners) {
            try {
                listener.notifyUpdate(this, key);
            } catch (Exception e) {
                LOGGER.error("notify " + listener + " with config change of " + key + " failed", e);
            }
        }
    }

    public String getApplicationKey() {
        return this.getString(APPLICATION_KEY_PROPERTY);
    }

    public String getApplicationName() {
        return this.getString(APPLICATION_NAME_PROPERTY);
    }

    public String getApplicationDirectory() {
        return this.getString(APPLICATION_DIRECTORY_PROPERTY);
    }

    public String getApplicationPath() {
        return this.getString(APPLICATION_PATH_PROPERTY);
    }

    public String getApplicationConfigDirectory() {
        return this.getString(APPLICATION_CONFIG_DIRECTORY_PROPERTY);
    }

    public Set<String> getReadOnlyKeys() {
        Set<String> readOnlyKeys = new HashSet<String>();
        for (Configuration configuration : configurations) {
            if (configuration instanceof ReadOnlyConfiguration) {
                readOnlyKeys.addAll(configuration.getKeys());
            }
        }
        return readOnlyKeys;
    }

    static ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

    public static ApplicationConfiguration get() {
        return applicationConfiguration;
    }
}
