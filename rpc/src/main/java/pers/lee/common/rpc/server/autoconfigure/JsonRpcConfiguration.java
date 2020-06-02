package pers.lee.common.rpc.server.autoconfigure;

import pers.lee.common.rpc.server.JsonRpcAnnotationProcessor;
import pers.lee.common.rpc.server.RpcBuilder;
import pers.lee.common.rpc.server.RpcConsole;
import pers.lee.common.rpc.server.web.SpringBootJsonBuilder;
import pers.lee.common.rpc.server.web.SpringBootJsonRpcFilter;
import pers.lee.common.rpc.server.web.SpringBootJsonRpcServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

import javax.servlet.Servlet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: Jay
 * @date: 2018/4/20
 */
@Configuration
public class JsonRpcConfiguration implements ImportAware {
    public static final Log LOGGER = LogFactory.getLog(RpcConsole.class);
    private AnnotationAttributes jsonRpcAttributes;

    @Bean
    public SpringBootJsonBuilder springBootJsonBuilder() {
        JsonRpcAnnotationProcessor jsonRpcAnnotationProcessor = new JsonRpcAnnotationProcessor();
        return new SpringBootJsonBuilder(jsonRpcAnnotationProcessor);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 50)
    public RegistrationBean jsonRpcRegistrationBean(SpringBootJsonBuilder springBootJsonBuilder) {
        RpcType rpcType = getRpcType();
        RegistrationBean registrationBean = null;
        if (RpcType.WEB_SERVLET.equals(rpcType)) {
            ServletRegistrationBean<Servlet> servletServletRegistrationBean = new ServletRegistrationBean();
            servletServletRegistrationBean.setName("springBootJsonRpcServlet");
            servletServletRegistrationBean.setServlet(new SpringBootJsonRpcServlet(springBootJsonBuilder));
            servletServletRegistrationBean.setUrlMappings(getUrlPattens());
            servletServletRegistrationBean.setInitParameters(getInitParameters(getRpcConfig()));
            servletServletRegistrationBean.setLoadOnStartup(1);
            registrationBean = servletServletRegistrationBean;
        } else if (RpcType.WEB_FILTER.equals(rpcType)) {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            filterRegistrationBean.setName("springBootJsonRpcFilter");
            filterRegistrationBean.setFilter(new SpringBootJsonRpcFilter(springBootJsonBuilder));
            filterRegistrationBean.setUrlPatterns(getUrlPattens());
            filterRegistrationBean.setInitParameters(getInitParameters(getRpcConfig()));
            registrationBean = filterRegistrationBean;
        }
        return registrationBean;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> attributes = importMetadata.getAnnotationAttributes(EnableJsonRpc.class.getName());
        this.jsonRpcAttributes = AnnotationAttributes.fromMap(attributes);
    }

    private String getRpcConfig() {
        String rpcConfig = jsonRpcAttributes.getString("rpcConfig");
        if (rpcConfig.trim().equals("")) {
            LOGGER.debug("json rpc config do not set");
            return null;
        }
        return rpcConfig;
    }

    private List<String> getUrlPattens() {
        String[] urlPatterns = (String[]) jsonRpcAttributes.get("urlPatterns");
        return Arrays.asList(urlPatterns);
    }

    private RpcType getRpcType() {
        return (RpcType) jsonRpcAttributes.get("type");
    }

    private Map<String, String> getInitParameters(String config) {
        return config == null ? Collections.emptyMap() : Collections.singletonMap(RpcBuilder.INIT_PARAMETER_RPC_CONFIG, config);
    }
}
