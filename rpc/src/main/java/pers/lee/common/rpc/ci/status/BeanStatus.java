package pers.lee.common.rpc.ci.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * BeanStatus
 *
 * @author Drizzt Yang
 */
public class BeanStatus implements StatusAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(BeanStatus.class);

    private String statusKey;
    private Object bean;
    private Map<String, Method> statusMethods;

    public BeanStatus(Object bean, String beanAlias) {
        this(bean, beanAlias, new HashMap<String, String>());
    }

    public BeanStatus(Object bean, String statusKey, Map<String, String> aliasMap) {
        this.statusKey = statusKey.startsWith("bean.") ? statusKey : "bean." + statusKey;
        this.bean = bean;
        this.statusMethods = new HashMap<String, Method>();

        Map<String, Method> propertyMethods = introspect();
        if (aliasMap == null) {
            aliasMap = new HashMap<String, String>();
        }
        for (Map.Entry<String, Method> propertyMethodEntry : propertyMethods.entrySet()) {
            String alias = aliasMap.get(propertyMethodEntry.getKey());
            if (alias != null) {
                statusMethods.put(alias, propertyMethodEntry.getValue());
            } else {
                statusMethods.put(propertyMethodEntry.getKey(), propertyMethodEntry.getValue());
            }
        }
    }

    @Override
    public String getStatusPrefix() {
        return statusKey;
    }

    @Override
    public Set<String> getStatusKeys() {
        return statusMethods.keySet();
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String> ();
        for (Map.Entry<String, Method> entry : statusMethods.entrySet()) {
            try {
                Method method = entry.getValue();
                Object statusValue = method.invoke(bean);
                if(statusValue != null) {
                    status.put(entry.getKey(), statusValue.toString());
                }
            } catch (Exception e) {
                LOGGER.warn("get status [" + entry.getKey() + "] of bean " + statusKey + " failed", e);
            } 
        }
        return status;
    }

    private static Set<Class> ACCEPT_PROPERTY_CLASSES = new HashSet<Class>();
    static {
        ACCEPT_PROPERTY_CLASSES.add(String.class);
        ACCEPT_PROPERTY_CLASSES.add(Boolean.class);
        ACCEPT_PROPERTY_CLASSES.add(Boolean.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Integer.class);
        ACCEPT_PROPERTY_CLASSES.add(Integer.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Double.class);
        ACCEPT_PROPERTY_CLASSES.add(Double.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Float.class);
        ACCEPT_PROPERTY_CLASSES.add(Float.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Long.class);
        ACCEPT_PROPERTY_CLASSES.add(Long.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Short.class);
        ACCEPT_PROPERTY_CLASSES.add(Short.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Byte.class);
        ACCEPT_PROPERTY_CLASSES.add(Byte.TYPE);
        ACCEPT_PROPERTY_CLASSES.add(Character.class);
        ACCEPT_PROPERTY_CLASSES.add(Character.TYPE);
    }          
    
    private Map<String, Method> introspect() {
        try {
            Map<String, Method> propertyMethods = new HashMap<String, Method>();
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if(propertyDescriptor.getReadMethod() == null) {
                    continue;
                }
                Class<?> propertyType = propertyDescriptor.getPropertyType();
                if(ACCEPT_PROPERTY_CLASSES.contains(propertyType)) {
                    propertyMethods.put(propertyDescriptor.getName(), propertyDescriptor.getReadMethod());
                }                
            }
            return propertyMethods;
        } catch (Exception e) {
            throw new RuntimeException("bean status digest failed", e);
        }
    }
}
