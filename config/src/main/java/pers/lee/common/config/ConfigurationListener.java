package pers.lee.common.config;

/**
 * ConfigurationListener
 *
 * @author Drizzt Yang
 */
public interface ConfigurationListener {

    /**
     * @param configuration
     */
    void notifyInit(Configuration configuration);

    /**
     * @param configuration
     * @param key
     */
    void notifyUpdate(Configuration configuration, String key);
}
