package pers.lee.commom.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Add the following code in main class
 * @SpringBootApplication(exclude = org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class)
 * @author: Jay.Lee
 * @date: 2018/11/26
 */
public class KafkaMessageListenerContainerRegistrar {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageListenerContainerRegistrar.class);

    private final KafkaProperties kafkaProperties;
    private final KafkaMessageListenerContainerRegistry containerRegistry;

    public KafkaMessageListenerContainerRegistrar(KafkaProperties kafkaProperties, KafkaMessageListenerContainerRegistry containerRegistry) {
        this.kafkaProperties = kafkaProperties;
        this.containerRegistry = containerRegistry;
    }

    public void registerContainers(List<String> topics, boolean autoStartUp) {
        List<KafkaListenerEndpoint> endpoints = getEndpoints(topics);
        containerRegistry.registerEndpoints(endpoints);
        if (autoStartUp || kafkaProperties.getConsumer().isAutoStartup()) {
            startAll();
        }
    }

    private List<KafkaListenerEndpoint> getEndpoints(List<String> topics) {
        return topics.stream().map(topic -> new KafkaListenerEndpointAdapter() {
            @Override
            public String getId() {
                return topic;
            }

            @Override
            public String getGroupId() {
                return Optional.ofNullable(kafkaProperties.getConsumer().getGroupId()).orElse(topic);
            }

            @Override
            public String getGroup() {
                return kafkaProperties.getConsumer().getGroupId();
            }

            @Override
            public Collection<String> getTopics() {
                return Collections.singletonList(topic);
            }

        }).collect(Collectors.toList());
    }

    private void startAll() {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (!container.isRunning()) {
                container.start();
                LOGGER.info("Container [{}] started", topic);
            }
        };
        invokeAll(action);
    }

    private void invokeAll(BiConsumer<String, MessageListenerContainer> containerConsumer) {
        containerRegistry.getValidListenerContainers().forEach(containerConsumer);
    }
}
