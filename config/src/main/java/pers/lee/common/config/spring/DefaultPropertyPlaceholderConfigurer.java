package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Passyt on 2018/3/24.
 */
public class DefaultPropertyPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer implements InitializingBean {

    private ApplicationConfiguration configuration;

    public DefaultPropertyPlaceholderConfigurer() {
        setLocalOverride(true);
    }

    @Override
    protected void convertProperties(Properties properties) {
        super.convertProperties(properties);
        String configLocationProperty = configuration.getApplicationKey();
        Properties systemProperties = System.getProperties();
        Enumeration propertyNames = systemProperties.propertyNames();

        while (propertyNames.hasMoreElements()) {
            String key = propertyNames.nextElement().toString();
            if (key.startsWith(configLocationProperty + ".")) {
                properties.put(key.replace(configLocationProperty + ".", ""), systemProperties.getProperty(key));
            }
        }
    }

    public void setConfiguration(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public void reload() {
        Properties properties = configuration.toProperties();
        convertProperties(properties);

        MutablePropertySources sources = (MutablePropertySources) getAppliedPropertySources();
        sources.addFirst(new PropertiesPropertySource(PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME, properties));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setProperties(configuration.toProperties());
    }
}
