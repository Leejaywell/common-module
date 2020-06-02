package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.json.TypeIntrospector;
import pers.lee.common.rpc.ci.management.IllegalEntityNameException;
import pers.lee.common.rpc.ci.management.ManagementService;
import org.apache.commons.lang3.ClassUtils;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author YangYang
 * @version 0.1, 2008-9-20 21:07:49
 */
@SuppressWarnings({"unchecked", "unused"})
public class BaseManagementService implements ManagementService {

    private static final Logger log = LoggerFactory.getLogger(BaseManagementService.class);

    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManager.getEntityManagerFactory();
    }

    public Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    public HashMap<String, Object> getEntities() {
        HashMap<String, Object> entityDescriptors = new HashMap<>();
        Set<EntityType<?>> entities = getEntityManagerFactory().getMetamodel().getEntities();
        for (EntityType<?> each : entities) {
            Object introspect = new TypeIntrospector().introspect(each.getJavaType());
            entityDescriptors.put(each.getJavaType().getName(), introspect);
        }

        return entityDescriptors;
    }

    @Override
    public Object get(String name, Map properties) {
        EntityType<?> entityType = getEntityType(name);
        javax.persistence.metamodel.Type<?> idType = entityType.getIdType();
        SingularAttribute<?, ?> idAttribute = entityType.getId(idType.getJavaType());
        Serializable identifierPropertyValue = Json.getDefault().toJavaObject(properties.get(idAttribute.getName()), idType.getJavaType());
        if (identifierPropertyValue != null) {
            return entityManager.find(entityType.getJavaType(), identifierPropertyValue);
        }
        return null;
    }

    @Override
    public Object put(String name, Map properties) {
        Object object = get(name, properties);
        Object identifier;
        properties = FlatPropertyUtils.structurize(properties);
        if (object == null) {
            // save object
            identifier = save(name, properties);
        } else {
            // update object
            identifier = getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(object);
            fill(new ObjectWrapper(object), properties);
            entityManager.merge(object);
        }
        return identifier;
    }


    protected EntityType<?> getEntityType(String name) {
        EntityType<?> entityType;
        try {
            entityType = entityManager.getMetamodel().entity(Class.forName(name));
        } catch (ClassNotFoundException e) {
            throw new IllegalEntityNameException(name);
        }
        return entityType;
    }

    protected Object save(String name, Map properties) {
        Class clazz = getEntityType(name).getJavaType();
        Object object = Json.getDefault().toJavaObject(properties, clazz);
        return entityManager.merge(object);
    }

    private void fill(ObjectWrapper objectWrapper, Map structuralProperties) {
        for (Object key : structuralProperties.keySet()) {
            String propertyName = (String) key;
            Object propertyValue = structuralProperties.get(key);

            Type propertyType = objectWrapper.getPropertyType(propertyName);
            if (propertyType == null) {
                continue;
            }

            if (propertyValue instanceof Map) {
                if (isExtend(propertyType, Map.class)) {
                    setNewValue(objectWrapper, propertyName, propertyValue);
                    continue;
                }

                Object oldPropertyValue = loadOldValue(objectWrapper, propertyName);
                if (oldPropertyValue == null) {
                    continue;
                }
                fill(new ObjectWrapper(oldPropertyValue), (Map) propertyValue);
                setNewValue(objectWrapper, propertyName, oldPropertyValue);
            } else if (propertyValue instanceof Collection) {
                if (isExtend(propertyType, Collection.class)) {
                    setNewValue(objectWrapper, propertyName, propertyValue);
                    continue;
                }
                throw new UnsupportedOperationException("not support update list with beans");
                // TODO: if the list contain entity
            } else {
                setNewValue(objectWrapper, propertyName, propertyValue);
            }
        }
    }

    private boolean isExtend(Type propertyType, Class superInterface) {
        if (propertyType == superInterface) {
            return true;
        }
        if (propertyType instanceof Class) {
            return ClassUtils.getAllInterfaces((Class) propertyType).contains(superInterface);
        }
        if (propertyType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) propertyType;
            return isExtend(parameterizedType.getRawType(), superInterface);
        } else {
            return false;
        }
    }

    private Object loadOldValue(ObjectWrapper objectWrapper, String propertyName) {
        Object oldPropertyValue = objectWrapper.getProperty(propertyName);

        if (oldPropertyValue instanceof HibernateProxy) {
            HibernateProxy hibernateProxy = (HibernateProxy) oldPropertyValue;
            oldPropertyValue = hibernateProxy.getHibernateLazyInitializer().getImplementation();
        }
        return oldPropertyValue;
    }

    private void setNewValue(ObjectWrapper objectWrapper, String propertyName, Object newValue) {
        Object newPropertyValue = Json.getDefault().toJavaObject(newValue, objectWrapper.getPropertyType(propertyName));
        objectWrapper.setProperty(propertyName, newPropertyValue);
    }

    @Override
    public void delete(String name, Map properties) {
        Object object = get(name, properties);
        if (object == null) {
            return;
        }
        entityManager.remove(object);
    }

    @Override
    public void batchDelete(String name, List<Map> entities) {
        for (Map properties : entities) {
            this.delete(name, properties);
        }
    }

    @Override
    public List<Object> batchPut(String name, List<Map> entities) {
        List<Object> result = new ArrayList<Object>();
        for (Map properties : entities) {
            result.add(this.put(name, properties));
        }
        return result;
    }

    @Override
    public List<Object> batchGet(String name, List<Map> batchProperties) {
        List<Object> entities = new ArrayList<Object>();
        for (Map properties : batchProperties) {
            entities.add(this.get(name, properties));
        }
        return entities;
    }

    class ObjectWrapper {
        Object bean;
        Type beanType;
        Map<String, PropertyDescriptor> propertyDescriptorMap;
        Map<String, Type> propertyTypeMap;

        ObjectWrapper(Object bean) {
            this.bean = bean;
            if (bean == null) {
                throw new IllegalArgumentException("bean is null or propertyName is null");
            }
            this.beanType = bean.getClass();
            this.propertyDescriptorMap = new HashMap<String, PropertyDescriptor>();
            this.propertyTypeMap = new HashMap<String, Type>();
            try {
                PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    Type type;
                    if (propertyDescriptor.getWriteMethod() != null) {
                        type = propertyDescriptor.getWriteMethod().getGenericParameterTypes()[0];
                    } else if (propertyDescriptor.getReadMethod() != null) {
                        type = propertyDescriptor.getReadMethod().getGenericReturnType();
                    } else {
                        continue;
                    }
                    propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
                    propertyTypeMap.put(propertyDescriptor.getName(), type);
                }
            } catch (IntrospectionException e) {
                throw new IllegalArgumentException("the class of bean [" + bean.getClass() + "] is unable to be introspected");
            }
        }

        public Type getPropertyType(String name) {
            return propertyTypeMap.get(name);
        }

        public Object getProperty(String name) {
            try {
                return propertyDescriptorMap.get(name).getReadMethod().invoke(bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getTargetException());
            }
        }

        public void setProperty(String name, Object propertyValue) {
            try {
                propertyDescriptorMap.get(name).getWriteMethod().invoke(bean, propertyValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getTargetException());
            }
        }

        public Type getBeanType() {
            return beanType;
        }

        public Object getBean() {
            return bean;
        }
    }

}
