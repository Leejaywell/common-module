package pers.lee.common.config.spring.utils;

import pers.lee.common.config.spring.ApplicationConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created by Jay.Lee on 2019/11/22 19:13
 */
public class ConfigUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    public static ApplicationConfigProperties buildApplicationConfigProperties(ConfigurableEnvironment environment) {
        ApplicationConfigProperties applicationConfigProperties = new ApplicationConfigProperties();
        Binder binder = Binder.get(environment);
        ResolvableType resolvableType = ResolvableType.forType(ApplicationConfigProperties.class);
        Bindable<?> target = Bindable.of(resolvableType).withExistingValue(applicationConfigProperties);
        BindResult<?> bind = binder.bind(ApplicationConfigProperties.PREFIX, target);
        logger.debug("application config propertis: {}", applicationConfigProperties);
        return applicationConfigProperties;
    }
}
