package pers.lee.commom.kafka.config;

import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.kafka.support.converter.MessageConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * @author: Jay.Lee
 * @date: 2019/3/11
 */
public interface KafkaListenerEndpointAdapter extends KafkaListenerEndpoint {

    @Override
    default String getId() {
        return null;
    }

    @Override
    default String getGroupId() {
        return null;
    }

    @Override
    default String getGroup() {
        return null;
    }

    @Override
    default Collection<String> getTopics() {
        return Collections.emptyList();
    }


    @Override
    default Collection<TopicPartitionInitialOffset> getTopicPartitions() {
        return Collections.emptyList();
    }

    @Override
    default Pattern getTopicPattern() {
        return null;
    }

    @Override
    default void setupListenerContainer(MessageListenerContainer listenerContainer, MessageConverter messageConverter){

    }
}
