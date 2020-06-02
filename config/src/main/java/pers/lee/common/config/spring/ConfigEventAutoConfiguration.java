package pers.lee.common.config.spring;

import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.config.spring.event.ConfigChangeEventPublisher;
import pers.lee.common.config.spring.event.ConfigProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Passyt on 2018/3/25.
 */
@Configuration
public class ConfigEventAutoConfiguration {

    @Bean
    public ConfigChangeEventPublisher publisher(@Autowired ApplicationConfiguration configuration, @Autowired ApplicationContext applicationContext) {
        ConfigChangeEventPublisher publisher = new ConfigChangeEventPublisher();
        publisher.setApplicationContext(applicationContext);
        publisher.setConfiguration(configuration);
        return publisher;
    }

    @Bean
    public ConfigProcessor configProcessor(@Autowired ApplicationConfiguration configuration) {
        ConfigProcessor processor = new ConfigProcessor();
        processor.setConfiguration(configuration);
        return processor;
    }

}
