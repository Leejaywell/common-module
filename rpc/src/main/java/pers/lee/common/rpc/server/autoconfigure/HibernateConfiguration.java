package pers.lee.common.rpc.server.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import pers.lee.common.rpc.ci.management.ManagementService;
import pers.lee.common.rpc.ci.management.impl.ComposedManagementService;
import pers.lee.common.rpc.ci.management.impl.ManagementServiceWrapper;
import pers.lee.common.rpc.ci.management.impl.SingleEntityServiceController;
import pers.lee.common.rpc.ci.management.impl.UniquePropertyAwareManagementService;
import pers.lee.common.rpc.ci.query.QueryService;
import pers.lee.common.rpc.ci.query.impl.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author: Jay
 * @date: 2018/5/14
 */
@Configuration
@ConditionalOnClass(value = {EntityManagerFactory.class})
@AutoConfigureAfter(value = {DataSource.class, EntityManagerFactory.class})
public class HibernateConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public SingleQueryServiceController singleQueryServiceController() {
        return new SingleQueryServiceController();
    }

    @Bean
    public SingleQueryServiceDiscovery singleQueryServiceDiscovery(SingleQueryServiceController singleQueryServiceController) {
        SingleQueryServiceDiscovery singleQueryServiceDiscovery = new SingleQueryServiceDiscovery();
        singleQueryServiceDiscovery.setSingleQueryServiceController(singleQueryServiceController);
        return singleQueryServiceDiscovery;
    }

    @Bean
    public HibernateQueryService hibernateQueryService() {
        HibernateQueryService hibernateQueryService = new HibernateQueryService();
        hibernateQueryService.setEntityManager(entityManager);
        return hibernateQueryService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "composedQueryService")
    public ComposedQueryService composedQueryService(HibernateQueryService hibernateQueryService, SingleQueryServiceController singleQueryServiceController) {
        ComposedQueryService composedQueryService = new ComposedQueryService();
        List<QueryService> queryServices = Arrays.asList(hibernateQueryService, singleQueryServiceController);
        composedQueryService.setQueryServices(queryServices);
        return composedQueryService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "queryService")
    public TransactionProxyFactoryBean queryService(ComposedQueryService composedQueryService, PlatformTransactionManager transactionManager) {
        TransactionProxyFactoryBean transactionProxyFactoryBean = new TransactionProxyFactoryBean();
        QueryServiceWrapper queryServiceWrapper = new QueryServiceWrapper();
        queryServiceWrapper.setQueryService(composedQueryService);
        transactionProxyFactoryBean.setTarget(queryServiceWrapper);
        transactionProxyFactoryBean.setTransactionManager(transactionManager);
        Properties properties = new Properties();
        properties.setProperty("query", "PROPAGATION_REQUIRES_NEW, readOnly");
        properties.setProperty("group", "PROPAGATION_REQUIRES_NEW, readOnly");
        properties.setProperty("delete", "PROPAGATION_REQUIRES_NEW");
        transactionProxyFactoryBean.setTransactionAttributes(properties);
        return transactionProxyFactoryBean;
    }

    @Bean
    public UniquePropertyAwareManagementService uniquePropertyAwareManagementService() {
        UniquePropertyAwareManagementService uniquePropertyAwareManagementService = new UniquePropertyAwareManagementService();
        uniquePropertyAwareManagementService.setEntityManager(entityManager);
        return uniquePropertyAwareManagementService;
    }

    @Bean
    public SingleEntityServiceController singleEntityServiceController() {
        return new SingleEntityServiceController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "composedManagementService")
    public ComposedManagementService composedManagementService(SingleEntityServiceController singleEntityServiceController, UniquePropertyAwareManagementService uniquePropertyAwareManagementService) {
        ComposedManagementService composedManagementService = new ComposedManagementService();
        uniquePropertyAwareManagementService.setEntityManager(entityManager);
        List<ManagementService> managementServices = Arrays.asList(uniquePropertyAwareManagementService, singleEntityServiceController);
        composedManagementService.setManagementServices(managementServices);
        return composedManagementService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "managementService")
    public TransactionProxyFactoryBean managementService(ComposedManagementService composedManagementService, PlatformTransactionManager transactionManager) {
        TransactionProxyFactoryBean transactionProxyFactoryBean = new TransactionProxyFactoryBean();
        ManagementServiceWrapper managementServiceWrapper = new ManagementServiceWrapper();
        managementServiceWrapper.setManagementService(composedManagementService);
        transactionProxyFactoryBean.setTarget(managementServiceWrapper);
        transactionProxyFactoryBean.setTransactionManager(transactionManager);
        Properties properties = new Properties();
        properties.setProperty("get", "PROPAGATION_REQUIRES_NEW, readOnly");
        properties.setProperty("put", "PROPAGATION_REQUIRES_NEW");
        properties.setProperty("delete", "PROPAGATION_REQUIRES_NEW");
        properties.setProperty("batchPut", "PROPAGATION_REQUIRES_NEW");
        properties.setProperty("batchDelete", "PROPAGATION_REQUIRES_NEW");
        transactionProxyFactoryBean.setTransactionAttributes(properties);
        return transactionProxyFactoryBean;
    }

}
