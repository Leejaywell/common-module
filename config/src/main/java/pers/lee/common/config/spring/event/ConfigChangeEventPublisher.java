package pers.lee.common.config.spring.event;

import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by Passyt on 2018/3/24.
 */
public class ConfigChangeEventPublisher implements InitializingBean, ConfigurationListener, ApplicationContextAware {

    private ApplicationConfiguration configuration;
    private ApplicationContext applicationContext;

    public ConfigChangeEventPublisher() {
    }

    public void afterPropertiesSet() throws Exception {
        this.configuration.addListener(this);
    }

    public void notifyInit(Configuration configuration) {
    }

    public void notifyUpdate(Configuration configuration, String key) {
        this.applicationContext.publishEvent(new ConfigChangeEvent(configuration, key, configuration.getString(key)));
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setConfiguration(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

}