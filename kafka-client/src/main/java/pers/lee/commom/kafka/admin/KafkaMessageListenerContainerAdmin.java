package pers.lee.commom.kafka.admin;

import pers.lee.commom.kafka.config.KafkaMessageListenerContainerRegistry;
import pers.lee.common.rpc.server.annotation.RpcMethod;
import pers.lee.common.rpc.server.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author: Jay
 * @date: 2018/7/18
 */
@RpcService("/kafka.rpc")
public class KafkaMessageListenerContainerAdmin {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageListenerContainerAdmin.class);
    private KafkaMessageListenerContainerRegistry kafkaMessageListenerContainerRegistry;

    public KafkaMessageListenerContainerAdmin(KafkaMessageListenerContainerRegistry kafkaMessageListenerContainerRegistry) {
        this.kafkaMessageListenerContainerRegistry = kafkaMessageListenerContainerRegistry;
    }

    @RpcMethod
    public Object start(String topicName) {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (!topic.equals(topicName)) return;
            if (!container.isRunning()) {
                container.start();
                LOGGER.info("Container [{}] started", topic);
            }
        };
        invokeAll(action);
        return "SUCCESS";
    }

    @RpcMethod
    public Object startAll() {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (!container.isRunning()) {
                container.start();
            }
            LOGGER.info("Container [{}] restarted", topic);
        };
        invokeAll(action);
        return "SUCCESS";
    }

    @RpcMethod
    public Object pause(String topicName) {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (container.isRunning()) {
                container.stop();
            }
            LOGGER.info("Container [{}] paused", topic);
        };
        invokeAll(action);
        return "SUCCESS";
    }

    @RpcMethod
    public Object resume(String topicName) {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (!container.isRunning()) {
                container.start();
            }
            LOGGER.info("Container [{}] resumed", topic);
        };
        invokeAll(action);
        return "SUCCESS";
    }

    @RpcMethod
    public Object restart(String topicName) {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (!topic.equals(topicName)) return;
            if (container.isRunning()) {
                container.stop();
                container.start();
                LOGGER.info("Container [{}] restarted", topic);
            }
            LOGGER.info("Container [{}] restarted", topic);
        };
        invokeAll(action);
        return "SUCCESS";
    }

    @RpcMethod
    public Object restartAll() {
        BiConsumer<String, MessageListenerContainer> action = (topic, container) -> {
            if (container.isRunning()) container.stop();
            container.start();
            LOGGER.info("Container [{}] restarted", topic);
        };
        invokeAll(action);
        return "SUCCESS";
    }

    @RpcMethod
    public Map<String, Map<String, Object>> listMetrics() {
        Map<String, Map<String, Object>> metrics = new HashMap<>();
        kafkaMessageListenerContainerRegistry.getValidListenerContainers().forEach((topic, messageListenerContainer) -> {
                    Map<String, Object> status = new HashMap<>();
                    messageListenerContainer.metrics().forEach((client, metricInfo) -> {
                        Map<String, Object> metricMap = new HashMap<>();
                        metricInfo.values().forEach(metric -> {
                            Map<String, Object> m1 = new HashMap<>();
                            m1.put("name", metric.metricName().name());
                            m1.put("description", metric.metricName().description());
                            m1.put("group", metric.metricName().group());
                            m1.put("tags", metric.metricName().tags());
                            m1.put("hashCode", metric.metricName().hashCode());
                            metricMap.put("metricName", m1);
                        });
                        status.put(client, metricMap);
                    });
                    status.put("isRunning", messageListenerContainer.isRunning());
                    metrics.put(topic, status);
                }
        );
        return metrics;
    }

    public Map<String, Boolean> listStatus() {
        Map<String, Boolean> containerStatus = new HashMap<>();
        kafkaMessageListenerContainerRegistry.getValidListenerContainers().forEach((topic, messageListener) ->
                containerStatus.put(topic, messageListener.isRunning())
        );
        return containerStatus;
    }

    private void invokeAll(BiConsumer<String, MessageListenerContainer> containerConsumer) {
        kafkaMessageListenerContainerRegistry.getValidListenerContainers().forEach(containerConsumer);
    }
}
