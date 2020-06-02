package pers.lee.common.rpc.server.autoconfigure;

import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import pers.lee.common.rpc.ci.management.EntityContext;
import pers.lee.common.rpc.ci.management.impl.MongoManagementService;
import pers.lee.common.rpc.ci.query.impl.MongoQueryServiceImpl;

/**
 * @author: Jay
 * @date: 2018/5/14
 */
@Configuration
@ConditionalOnClass(value = {MongoClient.class})
public class MongoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EntityContext entityContext(MappingContext mappingContext) {
        EntityContext entityContext = new EntityContext();
        entityContext.setMappingContext(mappingContext);
        return entityContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoManagementService managementService(MongoTemplate mongoTemplate, EntityContext entityContext) {
        MongoManagementService mongoManagementService = new MongoManagementService();
        mongoManagementService.setMongoTemplate(mongoTemplate);
        mongoManagementService.setEntityContext(entityContext);
        return mongoManagementService;
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoQueryServiceImpl queryService(MongoTemplate mongoTemplate, EntityContext entityContext) {
        MongoQueryServiceImpl mongoQueryService = new MongoQueryServiceImpl();
        mongoQueryService.setEntityContext(entityContext);
        mongoQueryService.setMongoTemplate(mongoTemplate);
        return mongoQueryService;
    }
}
