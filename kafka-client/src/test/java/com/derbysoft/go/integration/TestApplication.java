package com.derbysoft.go.integration;

import pers.lee.commom.kafka.annotation.EnableGoKafka;
import pers.lee.commom.kafka.config.KafkaContainerRegistrar;
import pers.lee.common.config.spring.EnableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.Collections;

/**
 * @author: Jay.Lee
 * @date: 2019/3/19
 */
@SpringBootApplication
@EnableConfig(applicationKey = "test")
@EnableGoKafka
public class TestApplication extends SpringBootServletInitializer {
    private Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TestApplication.class, args);
//        manualStatUp(applicationContext);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(TestApplication.class);
        return super.configure(builder);
    }

    /**
     * TODO
     * set go.kafka.consumer.auto-startup=false
     *
     * @param applicationContext
     */
    private static void manualStartUp(ConfigurableApplicationContext applicationContext) {
        KafkaContainerRegistrar registrar = applicationContext.getBean(KafkaContainerRegistrar.class);
        registrar.registerContainers(Collections.singletonList("test"), true);
    }

    @Bean
    public BatchMessageListener testMessageListener() {
        return (BatchMessageListener<String, String>) data -> data.forEach(consumerRecord -> {
            String message = String.format("send message key [%s], value[%s]", consumerRecord.key(), consumerRecord.value());
            log.info(message);
        });
    }
}
