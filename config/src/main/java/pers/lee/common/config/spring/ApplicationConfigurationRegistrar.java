package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Random;

/**
 * Created by Passyt on 2018/3/24.
 */
public class ApplicationConfigurationRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String[] WEB_ENVIRONMENT_CLASSES = {"javax.servlet.Servlet",
            "org.springframework.web.context.ConfigurableWebApplicationContext"};

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableConfig.class.getName());
        Object applicationKey = attributes.get("applicationKey");
        if (applicationKey == null) {
            throw new IllegalArgumentException("applicationKey is required in @EnableConfig on " + importingClassMetadata.getClassName());
        }

        System.setProperty(ApplicationConfiguration.APPLICATION_KEY_PROPERTY, String.valueOf(applicationKey));
        if (!isWebApplication()) {
            //init application configuration in WebConfigAutoConfiguration
            ApplicationConfiguration.get().init();
        }

        BeanDefinitionBuilder beanBuilder = BeanDefinitionBuilder.rootBeanDefinition(ApplicationConfiguration.class).setFactoryMethod("get");
        registry.registerBeanDefinition("applicationConfiguration", beanBuilder.getBeanDefinition());

        BeanDefinitionBuilder propertyPlaceholderConfigurer = BeanDefinitionBuilder.rootBeanDefinition(DefaultPropertyPlaceholderConfigurer.class);
        propertyPlaceholderConfigurer.addPropertyReference("configuration", "applicationConfiguration");
        registry.registerBeanDefinition(DefaultPropertyPlaceholderConfigurer.class.getSimpleName() + "@" + new Random().nextInt(), propertyPlaceholderConfigurer.getBeanDefinition());
    }

    private boolean isWebApplication() {
        for (String className : WEB_ENVIRONMENT_CLASSES) {
            if (!ClassUtils.isPresent(className, null)) {
                return false;
            }
        }
        return true;
    }

}
