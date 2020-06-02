package pers.lee.common.config.component;

import pers.lee.common.config.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * ReadOnlyConfiguration
 *
 * @author Drizzt Yang
 */
public class ReadOnlyConfiguration extends AbstractConfigurationWrapper {
    public ReadOnlyConfiguration(Configuration configuration) {
        this.setConfiguration(configuration);
    }

    @Override
    public void setProperty(String key, String value) {

    }

    @Override
    public void setProperty(String key, Boolean value) {

    }

    @Override
    public void setProperty(String key, Integer value) {

    }

    @Override
    public void setProperty(String key, Long value) {

    }

    @Override
    public void setProperty(String key, BigDecimal value) {

    }

    @Override
    public void setProperty(String key, BigInteger value) {

    }

    @Override
    public void setProperty(String key, List<String> value) {

    }

    @Override
    public void setProperty(String key, Set<String> value) {

    }

    @Override
    public void setProperty(String key, Map<String, String> value) {

    }

    @Override
    public void setProperty(String key, Properties value) {

    }

    @Override
    public void clearProperty(String key) {

    }
}
