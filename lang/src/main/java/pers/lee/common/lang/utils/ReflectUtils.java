package pers.lee.common.lang.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author YangYang
 * @version 0.1, 2008-6-29 23:42:34
 */
public class ReflectUtils {

    private static Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    public static final String PROPERTY_TYPE_STRICT = "STRICT";
    public static final String PROPERTY_TYPE_LOOSE = "LOOSE";

    public interface PropertyProcessor {
        public void process(BeanProperty beanProperty);
    }

    public static class BeanProperty {
        private Class<?> clazz;
        private String propertyName;
        private Method getMethod;
        private Method writeMethod;

        public BeanProperty(Class<?> clazz, String propertyName, Method getMethod, Method writeMethod) {
            this.clazz = clazz;
            this.propertyName = propertyName;
            this.getMethod = getMethod;
            this.writeMethod = writeMethod;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Type getPropertyType() {
            if (this.getMethod != null) {
                return this.getMethod.getGenericReturnType();
            } else {
                return this.writeMethod.getGenericParameterTypes()[0];
            }
        }

        public Class<?> getPropertyClass() {
            if (this.getMethod != null) {
                return this.getMethod.getReturnType();
            } else {
                return this.writeMethod.getParameterTypes()[0];
            }
        }

        public Class<?> getBeanClass() {
            return this.clazz;
        }

        public Object getPropertyValue(Object bean) {
            return invokeMethod(this.getMethod, bean);
        }

        public void setPropertyValue(Object bean, Object value) {
            invokeMethod(this.writeMethod, bean, value);
        }
    }

    public static boolean forIn(Class<?> clazz, PropertyProcessor propertyProcessor, String type) {
        PropertyDescriptor[] propertyDescriptors;
        try {
            propertyDescriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            log.error("Introspect class [" + clazz.getName() + "] failed", e);
            throw new RuntimeException(e);
        }

        List<PropertyDescriptor> descriptorList = new LinkedList<PropertyDescriptor>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method getMethod = propertyDescriptor.getReadMethod();
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (PROPERTY_TYPE_STRICT.equalsIgnoreCase(type)) {
                if (getMethod == null || writeMethod == null) {
                    continue;
                }
            }
            descriptorList.add(propertyDescriptor);
        }
        Collections.sort(descriptorList, new Comparator<PropertyDescriptor>() {
            @Override
            public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        boolean isHandled = false;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Method getMethod = propertyDescriptor.getReadMethod();
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (PROPERTY_TYPE_STRICT.equalsIgnoreCase(type)) {
                if (getMethod == null || writeMethod == null) {
                    continue;
                }
            }

            isHandled = true;
            propertyProcessor.process(new BeanProperty(clazz, propertyName, getMethod, writeMethod));
        }
        return isHandled;
    }

    public static boolean forIn(Class<?> clazz, PropertyProcessor propertyProcessor) {
        return forIn(clazz, propertyProcessor, PROPERTY_TYPE_STRICT);
    }

    @SuppressWarnings("unchecked")
    public static Set<Class> getInterfaces(Class<?> clazz) {
        Set<Class> iterfaces = new LinkedHashSet<Class>();
        iterfaces.addAll(Arrays.asList(clazz.getInterfaces()));
        for (Class superClass : getSuperClasses(clazz)) {
            iterfaces.addAll(Arrays.asList(superClass.getInterfaces()));
        }
        return iterfaces;
    }

    @SuppressWarnings("unchecked")
    public static Set<Class> getSuperClasses(Class clazz) {
        Set<Class> superClasses = new LinkedHashSet<Class>();
        while (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
            superClasses.add(clazz.getSuperclass());
            clazz = clazz.getSuperclass();
        }
        return superClasses;
    }

    public static Object invokeMethod(Method method, Object bean, Object... parameters) {
        boolean accessible = method.isAccessible();
        try {
            if (!accessible) {
                method.setAccessible(true);
            }
            return method.invoke(bean, parameters);
        } catch (IllegalAccessException e) {
            String message = "Invoke method [" + method + "] failed";
            if (log.isDebugEnabled()) {
                log.debug(message, e);
            }
            throw new RuntimeException(message, e);
        } catch (InvocationTargetException e) {
            String message = "Invoke method [" + method + "] failed";
            if (log.isDebugEnabled()) {
                log.debug(message, e.getTargetException());
            }
            throw new RuntimeException(message, e.getTargetException());
        } finally {
            method.setAccessible(accessible);
        }
    }

    @SuppressWarnings("unchecked")
    public static Collection<Object> newCollection(Class<?> clazz) {
        Collection<Object> collection;
        if (!clazz.isInstance(collection = new ArrayList<>())
                && !clazz.isInstance(collection = new LinkedHashSet<>())) {
            collection = (Collection<Object>) newObject(clazz);
        }
        return collection;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> newMap(Class<?> clazz) {
        Map<String, Object> newMap;
        if (!clazz.isInstance(newMap = new LinkedHashMap<>())) {
            newMap = (Map) newObject(clazz);
        }
        return newMap;
    }

    public static Object newObject(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("New an instance of [" + clazz + "] failed", e);
        }
    }


}
