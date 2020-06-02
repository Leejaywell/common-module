package pers.lee.common.config;

/**
 * SystemPropertiesListener
 *
 * @author Drizzt Yang
 */
public class SystemPropertiesListener implements ConfigurationListener {

    @Override
    public void notifyInit(Configuration configuration) {
        for (String key : configuration.getKeys()) {
            if (System.getProperty(key) != null &&
                    !System.getProperty(key).equals(configuration.getString(key))) {
                System.setProperty(key, configuration.getString(key));
            }
        }
    }

    @Override
    public void notifyUpdate(Configuration configuration, String key) {
        if (System.getProperty(key) != null) {
            if (configuration.getString(key) == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, configuration.getString(key));
            }
        }
    }

}
