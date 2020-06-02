package pers.lee.commom.kafka.config;

import java.util.List;

/**
 * @author: Jay.Lee
 * @date: 2018/11/26
 * @deprecated Use {@link KafkaMessageListenerContainerRegistrar#registerContainers(List,boolean)}
 */
@Deprecated
public class KafkaContainerRegistrar {

    private final KafkaMessageListenerContainerRegistrar registrar;

    public KafkaContainerRegistrar(KafkaMessageListenerContainerRegistrar registrar) {
        this.registrar = registrar;
    }

    /**
     * register consumers
     *
     * @param topics register topic
     */
    public void registerContainers(List<String> topics, boolean autoStart) {
        if (topics == null) {
            throw new IllegalArgumentException("topics is missing");
        }
        registrar.registerContainers(topics, autoStart);
    }

}
