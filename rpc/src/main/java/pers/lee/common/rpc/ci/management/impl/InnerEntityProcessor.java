package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.utils.ReflectUtils;

import javax.persistence.metamodel.EntityType;
import java.util.*;

/**
 * This is used to process the inner entity in the
 * UniquePropertyAwareManagementService at creation
 *
 * @see UniquePropertyAwareManagementService
 */
@SuppressWarnings("unchecked")
public class InnerEntityProcessor implements ReflectUtils.PropertyProcessor {
    private Object object;
    private UniquePropertyAwareManagementService managementService;
    private List<Class> classPool;

    public InnerEntityProcessor(Object object, UniquePropertyAwareManagementService managementService) {
        this.object = object;
        this.managementService = managementService;
        classPool = new ArrayList();
    }

    public InnerEntityProcessor(Object object, InnerEntityProcessor innerEntityProcessor) {
        this.object = object;
        this.managementService = innerEntityProcessor.managementService;
        this.classPool = innerEntityProcessor.classPool;
    }

    public void process(ReflectUtils.BeanProperty beanProperty) {
        Object propertyValue = beanProperty.getPropertyValue(object);
        if (propertyValue == null) {
            return;
        }
        Class propertyClass = beanProperty.getPropertyClass();
        if (isIgnored(propertyClass)) {
            return;
        }
        EntityType entityType = getEntityType(propertyClass);
        if (entityType == null) {
            if (classPool.contains(propertyClass)) {
                return;
            }
            ReflectUtils.forIn(propertyClass, new InnerEntityProcessor(propertyValue, this));
            classPool.add(propertyClass);
            return;
        }
        if (managementService.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(propertyValue) == null) {
            Map properties = (Map) Json.getDefault().toJsonObject(propertyValue);
            Object entity = managementService.get(beanProperty.getPropertyClass().getName(), new HashMap(properties));
            beanProperty.setPropertyValue(object, entity);
        }
    }

    private EntityType getEntityType(Class propertyClass) {
        Set<EntityType<?>> entities = managementService.getEntityManagerFactory().getMetamodel().getEntities();
        Optional<EntityType<?>> optional = entities.stream().filter(entityType -> entityType.getJavaType() == propertyClass).findFirst();
        return optional.orElse(null);
    }

    private boolean isIgnored(Class propertyClass) {
        if (propertyClass.getPackage() != null && propertyClass.getPackage().getName() != null) {
            return propertyClass.getPackage().getName().contains("java.lang");
        }
        return true;
    }

}
