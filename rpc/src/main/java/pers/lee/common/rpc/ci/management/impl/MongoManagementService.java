package pers.lee.common.rpc.ci.management.impl;

import pers.lee.common.lang.json.Json;
import pers.lee.common.rpc.ci.management.EntityContext;
import pers.lee.common.rpc.ci.management.ManagementService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rubys@vip.qq.com
 * @since 2013-8-12
 */
public class MongoManagementService implements ManagementService {

    private EntityContext entityContext;
    private MongoTemplate mongoTemplate;

    @Override
    public HashMap<String, Object> getEntities() {
        return entityContext.getEntities();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object get(String entityName, Map properties) {
        MongoPersistentEntity<?> entity = entityContext.getPersistentEntity(entityName);
        Class<?> entityClass = entityContext.getEntityClass(entityName);
        Serializable identifierPropertyValue = getIdentifierValue(entity, properties, entityClass);
        if (identifierPropertyValue != null) {
            return mongoTemplate.findById(identifierPropertyValue, entityClass);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Object put(String entityName, Map properties) {
        MongoPersistentEntity<?> entity = entityContext.getPersistentEntity(entityName);
        Class<?> entityClass = entityContext.getEntityClass(entityName);
        Assert.notNull(getIdentifierValue(entity, properties, entityClass));
        mongoTemplate.save(Json.getDefault().toJavaObject(properties, entityClass));
        return properties;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void delete(String entityName, Map properties) {
        MongoPersistentEntity<?> entity = entityContext.getPersistentEntity(entityName);
        Class<?> entityClass = entityContext.getEntityClass(entityName);
        Serializable identifierPropertyValue = getIdentifierValue(entity, properties, entityClass);
        if (identifierPropertyValue == null) {
            return;
        }
        mongoTemplate.remove(Query.query(Criteria.where("_id").is(identifierPropertyValue)), entityClass);
    }

    @Override
    public List<Object> batchGet(String name, List<Map> batchProperties) {
        List<Object> entities = new ArrayList<Object>();
        for (Map properties : batchProperties) {
            entities.add(this.get(name, properties));
        }
        return entities;
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
    public void batchDelete(String name, List<Map> entities) {
        for (Map properties : entities) {
            this.delete(name, properties);
        }
    }

    @SuppressWarnings("rawtypes")
    private Serializable getIdentifierValue(MongoPersistentEntity<?> entity, Map properties, Class<?> entityClass) {
        String identifierPropertyName = entity.getIdProperty().getField().getName();
        return (Serializable) Json.getDefault().toJavaObject(properties.get(identifierPropertyName), String.class);
    }

    @Required
    public void setEntityContext(EntityContext entityContext) {
        this.entityContext = entityContext;
    }

    @Required
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public EntityContext getEntityContext() {
        return entityContext;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
