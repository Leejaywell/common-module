package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.web.context.support.ServletContextPropertySource;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Jay.Lee on 2019/11/22 15:26
 */
public class ApplicationConfigEnvironmentProcessor implements EnvironmentPostProcessor, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigEnvironmentProcessor.class);
    private String applicationKey = null;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isEnable(application.getMainApplicationClass())) {
            logger.info("application config is not enable on {}", application.getMainApplicationClass().getName());
            return;
        }
        resolveApplicationKey(application);
        if (applicationKey == null) {
            throw new IllegalArgumentException("applicationKey is required in @EnableConfig on " + application.getMainApplicationClass().getName());
        }
        application.addInitializers(new ApplicationConfigInitializer(applicationKey, application));
    }

    private void resolveApplicationKey(SpringApplication application) {
        Class<?> applicationClass = application.getMainApplicationClass();
        EnableConfig enableConfig = AnnotationUtils.findAnnotation(applicationClass, EnableConfig.class);
        applicationKey = (String) AnnotationUtils.getValue(enableConfig, "applicationKey");
    }

    private boolean isEnable(Class<?> applicationClass) {
        return AnnotationUtils.isAnnotationDeclaredLocally(EnableConfig.class, applicationClass);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }

    private static class ApplicationConfigInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        private String applicationKey;
        private SpringApplication application;
        private ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.get();

        public ApplicationConfigInitializer(String applicationKey, SpringApplication application) {
            this.applicationKey = applicationKey;
            this.application = application;
        }

        public ApplicationConfigInitializer(String applicationKey, SpringApplication application, ApplicationConfiguration applicationConfiguration) {
            this.applicationKey = applicationKey;
            this.application = application;
            this.applicationConfiguration = applicationConfiguration;
        }

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            initialize(environment);
            addPropertySources(environment);
        }

        void initialize(ConfigurableEnvironment environment) {
            ServletContextPropertySource servletContextPropertySource = getServletContextPropertySource(environment, application);
            Map<String, String> props = new HashMap<>();
            if (applicationKey != null) {
                props.put(ApplicationConfiguration.APPLICATION_KEY_PROPERTY, applicationKey);
            }
            if (servletContextPropertySource != null) {
                initWebProps(servletContextPropertySource.getSource(), props);
            }
            applicationConfiguration.init(props);
        }

        private void addPropertySources(ConfigurableEnvironment environment) {
            MutablePropertySources propertySources = environment.getPropertySources();
            Properties properties = applicationConfiguration.toProperties();
            propertySources.addAfter(RandomValuePropertySource.RANDOM_PROPERTY_SOURCE_NAME, new PropertiesPropertySource(ApplicationConfiguration.APPLICATION_CONFIG, properties));
        }

        private void initWebProps(ServletContext servletContext, Map<String, String> properties) {
            String applicationDirectory = servletContext.getRealPath("/");
            if (properties == null) {
                properties = new HashMap<>();
            }
            if (applicationDirectory != null) {
                properties.put(ApplicationConfiguration.APPLICATION_DIRECTORY_PROPERTY, applicationDirectory);
            }
            String contextPath = servletContext.getContextPath();
            properties.put(ApplicationConfiguration.APPLICATION_PATH_PROPERTY, contextPath);

            if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
                properties.put(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY, "ROOT");
            } else {
                properties.put(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY, contextPath.substring(1));
            }

//            if (!configuration.containsKey(ApplicationConfiguration.APPLICATION_NAME_PROPERTY)) {
//                properties.put(ApplicationConfiguration.APPLICATION_NAME_PROPERTY, properties.get(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY));
//            }
            properties.put(ApplicationConfiguration.APPLICATION_NAME_PROPERTY, properties.get(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY));
        }

        private ServletContextPropertySource getServletContextPropertySource(ConfigurableEnvironment environment, SpringApplication application) {
            ServletContextPropertySource servletContextPropertySource = null;
            WebApplicationType webApplicationType = application.getWebApplicationType();
            if (webApplicationType == WebApplicationType.SERVLET) {
                StandardServletEnvironment standardServletEnvironment = (StandardServletEnvironment) environment;
                MutablePropertySources propertySources = standardServletEnvironment.getPropertySources();
                for (PropertySource<?> propertySource : propertySources) {
                    if (propertySource instanceof ServletContextPropertySource) {
                        servletContextPropertySource = (ServletContextPropertySource) propertySource;
                    }
                }
            }
            return servletContextPropertySource;
        }
    }

}
