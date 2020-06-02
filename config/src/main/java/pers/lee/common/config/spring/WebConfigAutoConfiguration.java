package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Passyt on 2018/3/25.
 */
@ConditionalOnWebApplication
public class WebConfigAutoConfiguration {

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> servletListenerRegistrationBean(WebApplicationContext context, ApplicationConfiguration configuration) {
        ServletContext servletContext = context.getServletContext();

        String applicationDirectory = servletContext.getRealPath("/");
        Map<String, String> props = new HashMap<>();
        if (applicationDirectory != null) {
            props.put(ApplicationConfiguration.APPLICATION_DIRECTORY_PROPERTY, applicationDirectory);
        }
        String contextPath = servletContext.getContextPath();
        props.put(ApplicationConfiguration.APPLICATION_PATH_PROPERTY, contextPath);

        if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
            props.put(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY, "ROOT");
        } else {
            props.put(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY, contextPath.substring(1));
        }

        if (!configuration.containsKey(ApplicationConfiguration.APPLICATION_NAME_PROPERTY)) {
            props.put(ApplicationConfiguration.APPLICATION_NAME_PROPERTY, props.get(ApplicationConfiguration.APPLICATION_ALIAS_PROPERTY));
        }
        configuration.init(props);

        DefaultPropertyPlaceholderConfigurer bean = context.getBean(DefaultPropertyPlaceholderConfigurer.class);
        bean.reload();

        ServletListenerRegistrationBean<ServletContextListener> registrationBean = new ServletListenerRegistrationBean<ServletContextListener>(new WebConfigServletContextListener());
        return registrationBean;
    }

    private static class WebConfigServletContextListener implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent sce) {
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
        }
    }

}
