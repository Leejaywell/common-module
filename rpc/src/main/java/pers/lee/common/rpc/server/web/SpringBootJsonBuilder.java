package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.JsonRpcAnnotationProcessor;
import pers.lee.common.rpc.server.JsonRpcContext;
import pers.lee.common.rpc.server.RpcConfig;
import pers.lee.common.rpc.server.SpringBeanAnnotationMethodInvokerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.MethodMetadata;

import javax.servlet.ServletContext;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.util.ClassUtils.forName;

/**
 * @author: Jay
 * @date: 2018/4/24
 */
public class SpringBootJsonBuilder extends SpringJsonRpcBuilder implements ApplicationContextAware, BeanFactoryPostProcessor, InitializingBean {
    private ApplicationContext applicationContext;
    private JsonRpcAnnotationProcessor jsonRpcAnnotationProcessor;

    public SpringBootJsonBuilder(JsonRpcAnnotationProcessor jsonRpcAnnotationProcessor) {
        this.jsonRpcAnnotationProcessor = jsonRpcAnnotationProcessor;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            Class<?> beanClass = getBeanClass(beanDefinition, beanFactory.getBeanClassLoader());
            if(beanClass == null) {
                continue;
            }
            jsonRpcAnnotationProcessor.registerMetaInfo(beanClass);
        }
    }

    @Override
    public JsonRpcContext build(Map<String, String> initParameters, ServletContext servletContext) {
        JsonRpcContext jsonRpcContext = super.build(initParameters, servletContext);
        RpcConfig rpcConfig = jsonRpcAnnotationProcessor.process();
        jsonRpcContext.getRpcConfig().setDomains(rpcConfig.getDomains());
        jsonRpcContext.getRpcConfig().setRpcMap(rpcConfig.getRpcMap());
        return jsonRpcContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        if (jsonRpcAnnotationProcessor.getMethodInvokerFactory() == null) {
            jsonRpcAnnotationProcessor.setMethodInvokerFactory(new SpringBeanAnnotationMethodInvokerFactory(applicationContext));
        }
        if (jsonRpcAnnotationProcessor.getRpcInvokerFactory() == null) {
            jsonRpcAnnotationProcessor.setRpcInvokerFactory(new JsonRpcInvokerFactory());
        }
    }

    private Class<?> getBeanClass(BeanDefinition serviceBeanDefinition, ClassLoader beanClassLoader) {
        String beanClassName = serviceBeanDefinition.getBeanClassName();
        if (beanClassName == null) {
            if (serviceBeanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = AnnotatedBeanDefinition.class.cast(serviceBeanDefinition);
                MethodMetadata metadata = annotatedBeanDefinition.getFactoryMethodMetadata();
                if (metadata != null) {
                    beanClassName = metadata.getReturnTypeName();
                }
            }
        }
        if (beanClassName == null) {
            return null;
        }
        try {
            return forName(beanClassName, beanClassLoader);
        } catch (ClassNotFoundException | LinkageError e) {
            throw new RuntimeException(format("Cannot find bean class '%s'.", beanClassName), e);
        }
    }

}
