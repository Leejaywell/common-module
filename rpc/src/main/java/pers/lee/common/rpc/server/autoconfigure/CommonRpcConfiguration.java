package pers.lee.common.rpc.server.autoconfigure;

import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.rpc.CommonInterfaceFilter;
import pers.lee.common.rpc.ci.config.ConfigService;
import pers.lee.common.rpc.ci.detect.DetectorManager;
import pers.lee.common.rpc.ci.detect.SqlDetector;
import pers.lee.common.rpc.ci.spring.BeanStatusWatch;
import pers.lee.common.rpc.ci.spring.SpringDetectorInitializing;
import pers.lee.common.rpc.ci.spring.SpringLogStatusInitializing;
import pers.lee.common.rpc.ci.status.MySQLStatus;
import pers.lee.common.rpc.ci.status.MySQLTableStatus;
import pers.lee.common.rpc.ci.status.StatusCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * @author: Jay
 * @date: 2018/4/20
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(value = {CommonInterfaceFilter.class})
public class CommonRpcConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public FilterRegistrationBean commonRPCFilter() {
        CommonInterfaceFilter commonInterfaceFilter = new CommonInterfaceFilter();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(commonInterfaceFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("commonInterfaceFilter");
        return filterRegistrationBean;
    }

    @ConditionalOnClass(value = ConfigService.class)
    @Bean
    public ConfigService configService() {
        return ConfigService.get();
    }

    @ConditionalOnClass(value = StatusCenter.class)
    @Bean
    public StatusCenter statusCenter() {
        return StatusCenter.get();
    }

    @ConditionalOnClass(value = BeanStatusWatch.class)
    @Bean
    @DependsOn(value = "statusCenter")
    public BeanStatusWatch beanStatusWatch(StatusCenter statusCenter) {
        BeanStatusWatch beanStatusWatch = new BeanStatusWatch();
        beanStatusWatch.setStatusCenter(statusCenter);
        return beanStatusWatch;
    }

    @ConditionalOnBean(value = ApplicationConfiguration.class)
    @Bean
    public SpringLogStatusInitializing springLogStatusInitializing(ApplicationConfiguration applicationConfiguration, StatusCenter statusCenter) {
        SpringLogStatusInitializing springLogStatusInitializing = new SpringLogStatusInitializing();
        springLogStatusInitializing.setApplicationConfiguration(applicationConfiguration);
        springLogStatusInitializing.setStatusCenter(statusCenter);
        return springLogStatusInitializing;
    }

    @ConditionalOnClass(value = DetectorManager.class)
    @Bean
    public DetectorManager detectorManager() {
        return DetectorManager.get();
    }

    @ConditionalOnClass(value = SpringDetectorInitializing.class)
    @Bean
    @DependsOn(value = "detectorManager")
    public SpringDetectorInitializing springDetectorInitializing(DetectorManager detectorManager, ApplicationContext applicationContext) {
        SpringDetectorInitializing springDetectorInitializing = new SpringDetectorInitializing();
        springDetectorInitializing.setDetectorManager(detectorManager);
        springDetectorInitializing.setApplicationContext(applicationContext);
        return springDetectorInitializing;
    }

    @ConditionalOnBean(value = DataSource.class)
    @Bean
    public MySQLStatus mySQLStatus(@Autowired DataSource dataSource) {
        return new MySQLStatus(dataSource);
    }

    @ConditionalOnBean(value = DataSource.class)
    @Bean
    public SqlDetector sqlDetector(@Autowired DataSource dataSource) {
        return new SqlDetector("mysql", dataSource, "select 0;");
    }

    @ConditionalOnBean(value = DataSource.class)
    @Bean
    public MySQLTableStatus mySQLTableStatus(@Autowired DataSource dataSource) {
        return new MySQLTableStatus(dataSource);
    }


}
