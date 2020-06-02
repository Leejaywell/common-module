package pers.lee.commom.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: Jay.Lee
 * @date: 2019/3/12
 */
public class KafkaMessageListenerContainerRegistry {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final ConcurrentKafkaListenerContainerFactory containerFactory;

    Map<String, MessageListenerContainer> validListenerContainers = new ConcurrentHashMap<>();

    public KafkaMessageListenerContainerRegistry(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, ConcurrentKafkaListenerContainerFactory containerFactory) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.containerFactory = containerFactory;
    }

    /**
     * register consumers
     *
     * @param kafkaListenerEndpoints register topic
     */
    public void registerEndpoints(List<KafkaListenerEndpoint> kafkaListenerEndpoints) {
        if (kafkaListenerEndpoints == null) {
            throw new IllegalArgumentException("kafkaListenerEndpoints is missing");
        }
        //stop the container of removed topic
        List<String> endpoints = kafkaListenerEndpoints.stream().map(KafkaListenerEndpoint::getId).collect(Collectors.toList());
        stopEndpoint(endpoints);
        startEndpoint(kafkaListenerEndpoints);
        getValidContainers(endpoints);
    }

    private void getValidContainers(List<String> endpoints) {
        validListenerContainers.clear();
        getListenerContainers().forEach((endpoint, messageListenerContainer) -> {
            if (endpoints.contains(endpoint))
                validListenerContainers.put(endpoint, messageListenerContainer);
        });
    }

    private void startEndpoint(List<KafkaListenerEndpoint> kafkaListenerEndpoints) {
        kafkaListenerEndpoints.forEach(kafkaListenerEndpoint -> {
            MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer(kafkaListenerEndpoint.getId());
            if (container != null) {
                if (!container.isRunning()) {
                    logger.info("restart container [{}]", kafkaListenerEndpoint.getId());
                    container.start();
                }
                return;
            }
            kafkaListenerEndpointRegistry.registerListenerContainer(kafkaListenerEndpoint, containerFactory);
        });
    }

    private void stopEndpoint(List<String> endpoints) {
        getListenerContainers().forEach((endpoint, messageListenerContainer) -> {
                    if (!endpoints.contains(endpoint) && messageListenerContainer.isRunning()) {
                        messageListenerContainer.stop(() -> logger.info("stop container[{}]", endpoint));
                    }
                }
        );
    }

    private Map<String, MessageListenerContainer> getListenerContainers() {
        Map<String, MessageListenerContainer> listenerContainers = new ConcurrentHashMap<>();
        kafkaListenerEndpointRegistry.getListenerContainerIds().forEach(id -> listenerContainers.put(id, kafkaListenerEndpointRegistry.getListenerContainer(id)));
        return listenerContainers;
    }

    public Map<String, MessageListenerContainer> getValidListenerContainers() {
        return validListenerContainers;
    }
}
