package pers.lee.common.rpc.ci.management;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EntityContext {

    private Map<String, Class<?>> entityClassMapping = new HashMap<>();
    private Map<String, MongoPersistentEntity<?>> persistentEntityMapping = new HashMap<>();
    private MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

    public Class<?> getEntityClass(String entityName) {
        return entityClassMapping.get(entityName);
    }

    public void addPersistentEntity(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        entityClassMapping.put(simpleName, clazz);
        persistentEntityMapping.put(simpleName, mappingContext.getPersistentEntity(clazz));
    }

    public HashMap<String, Object> getEntities() {
        HashMap<String, Object> entityDescriptors = new HashMap<String, Object>();
        for (Entry<String, Class<?>> entry : entityClassMapping.entrySet()) {
            entityDescriptors.put(entry.getKey(), "UNKNOWN");
        }
        return entityDescriptors;
    }

    public String getCollection(String entityName) {
        return persistentEntityMapping.get(entityName).getCollection();
    }

    public MongoPersistentEntity<?> getPersistentEntity(String entityName) {
        return persistentEntityMapping.get(entityName);
    }

    @Required
    public void setMappingContext(MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {
        this.mappingContext = mappingContext;
        mappingContext.getPersistentEntities().forEach(a -> {
            if (!a.getType().getName().contains("$")) {
                addPersistentEntity(a.getType());
            }
        });
    }

}
