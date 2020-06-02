package pers.lee.commom.kafka.listener;

import pers.lee.commom.kafka.config.KafkaMessageListenerContainerRegistrar;
import pers.lee.commom.kafka.model.TopicEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author: Jay.Lee
 * @date: 2018/11/28
 */
public class TopicEventListener implements ApplicationListener<TopicEvent> {

    private KafkaMessageListenerContainerRegistrar kafkaMessageListenerContainerRegistrar;

    public TopicEventListener(KafkaMessageListenerContainerRegistrar kafkaMessageListenerContainerRegistrar) {
        this.kafkaMessageListenerContainerRegistrar = kafkaMessageListenerContainerRegistrar;
    }

    @Override
    public void onApplicationEvent(TopicEvent event) {
        kafkaMessageListenerContainerRegistrar.registerContainers(event.getTopics(), false);
    }
}
