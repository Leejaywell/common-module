package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.config.spring.event.ConfigChangeEvent;
import pers.lee.common.lang.json.Json;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Passyt on 2018/3/24.
 */
public class BeanConfigurableProperty implements ApplicationListener<ConfigChangeEvent>, BeanPostProcessor, BeanFactoryAware {
    private static Logger LOGGER = LoggerFactory.getLogger(BeanConfigurableProperty.class);

    private BeanFactory beanFactory;
    private ApplicationConfiguration applicationConfiguration;
    private String key;
    private Object bean;
    private String beanName;
    private Method updateMethod;
    private String updateMethodName;

    private boolean init = true;

    public BeanConfigurableProperty() {
    }

    public BeanConfigurableProperty(String key, Object bean, Method updateMethod) {
        this.key = key;
        this.bean = bean;
        this.updateMethod = updateMethod;
    }

    public BeanConfigurableProperty(String key, String beanName, String updateMethodName) {
        this.key = key;
        this.beanName = beanName;
        this.updateMethodName = updateMethodName;
    }

    public BeanConfigurableProperty(String key, String beanName, String updateMethodName, boolean init) {
        this(key, beanName, updateMethodName);
        this.init = init;
    }

    @Override
    public void onApplicationEvent(ConfigChangeEvent configChangeEvent) {
        if (!configChangeEvent.getKey().equals(key)) {
            return;
        }

        if (updateMethod == null) {
            initUpdateMethod(null);
        }
        try {
            invokeSetValue(configChangeEvent.getValue());
        } catch (Exception e) {
            LOGGER.error("update value [0] failed for [1]", new Object[]{configChangeEvent.getValue(), this.toString()});
        }
    }

    private void initUpdateMethod(Object bean) {
        if (bean != null) {
            this.bean = bean;
        } else {
            this.bean = beanFactory.getBean(beanName);
        }
        for (Method method : this.bean.getClass().getMethods()) {
            if (method.getName().equals(updateMethodName) && method.getParameterTypes().length == 1) {
                this.updateMethod = method;
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!beanName.equals(this.beanName)) {
            return bean;
        }
        if (updateMethod == null) {
            initUpdateMethod(bean);
        }
        if (!init) {
            return bean;
        }

        LOGGER.debug("init config for bean {} with id {}", bean, beanName);
        try {
            invokeSetValue(applicationConfiguration.getString(key));
        } catch (Exception e) {
            throw new IllegalStateException("init value " + applicationConfiguration.getString(key) + " failed for " + this.toString(),
                    e.getCause());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    public void invokeSetValue(String value) throws IllegalAccessException, InvocationTargetException {
        LOGGER.debug("set config property {} of bean {} using method {}", new Object[]{key, bean, updateMethod.getName()});
        Object bean = getBean();
        Class<?> parameterOfMethod = updateMethod.getParameterTypes()[0];
        if (parameterOfMethod.equals(String.class)) {
            updateMethod.invoke(bean, value);
            return;
        }
        if (parameterOfMethod.isPrimitive()) {
            parameterOfMethod = ClassUtils.primitiveToWrapper(parameterOfMethod);
        }
        Method valueOfMethod = getValueOfMethod(parameterOfMethod);
        if (valueOfMethod != null) {
            updateMethod.invoke(bean, valueOfMethod.invoke(null, value));
            return;
        }
        Constructor<?> constructor = getValueConstructor(parameterOfMethod);
        if (constructor != null) {
            updateMethod.invoke(bean, newInstance(value, constructor));
            return;
        }
        try {
            updateMethod.invoke(bean, Json.getDefault().deserialize(new StringReader(value), parameterOfMethod));
        } catch (Exception e) {
            throw new RuntimeException("Illegal config value " + value);
        }
    }

    private Object newInstance(String value, Constructor<?> constructor) throws IllegalAccessException, InvocationTargetException {
        try {
            return constructor.newInstance(value);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    private Constructor<?> getValueConstructor(Class<?> parameterOfMethod) {
        try {
            return parameterOfMethod.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Method getValueOfMethod(Class<?> clazz) {
        try {
            return clazz.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Bean " + bean.getClass() + ", property update method " + updateMethod.getName() + " with key " + key;
    }

    public String getKey() {
        return key;
    }

    public Object getBean() {
        return bean;
    }

    public Method getUpdateMethod() {
        return updateMethod;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}