package pers.lee.common.config.spring.event;

import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.config.Config;
import pers.lee.common.config.spring.BeanConfigurableProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Passyt on 2018/3/24.
 */
public class ConfigProcessor implements BeanPostProcessor, ApplicationListener<ConfigChangeEvent> {

    private static final Log log = LogFactory.getLog(ConfigProcessor.class);

    private Map<String, List<BeanConfigurableProperty>> configurationLinkPoints = new HashMap<String, List<BeanConfigurableProperty>>();

    private ApplicationConfiguration applicationConfiguration;

    @Override
    public void onApplicationEvent(ConfigChangeEvent configChangeEvent) {
        List<BeanConfigurableProperty> properties = configurationLinkPoints.get(configChangeEvent.getKey());
        if (properties == null) {
            return;
        }
        for (BeanConfigurableProperty property : properties) {
            try {
                property.invokeSetValue(configChangeEvent.getValue());
            } catch (Exception e) {
                log.error("Set config of link point [key " + configChangeEvent.getKey() + ", value " + configChangeEvent.getValue() + "] to "
                        + property + " error", e);
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Method method : beanClass.getMethods()) {
            Config config = AnnotationUtils.getAnnotation(method, Config.class);
            if (config != null) {
                initBeanConfigurableProperty(config.value(), bean, method);
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void initBeanConfigurableProperty(String configKey, Object bean, Method writeMethod) {
        List<BeanConfigurableProperty> properties = configurationLinkPoints.get(configKey);
        if (properties == null) {
            properties = new ArrayList<BeanConfigurableProperty>();
            configurationLinkPoints.put(configKey, properties);
        }

        BeanConfigurableProperty beanConfigurableProperty = new BeanConfigurableProperty(configKey, bean, writeMethod);
        properties.add(beanConfigurableProperty);
        try {
            beanConfigurableProperty.invokeSetValue(applicationConfiguration.getString(configKey));
        } catch (Exception e) {
            throw new IllegalStateException("init value " + applicationConfiguration.getString(configKey)
                    + " failed for " + beanConfigurableProperty.toString(), e.getCause());
        }
    }

    public void setConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }
}
