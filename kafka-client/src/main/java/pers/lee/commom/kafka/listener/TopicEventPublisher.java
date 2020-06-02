package pers.lee.commom.kafka.listener;

import pers.lee.commom.kafka.ConfigKeys;
import pers.lee.commom.kafka.model.TopicEvent;
import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.config.Configuration;
import pers.lee.common.config.ConfigurationListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Jay.Lee
 * @date: 2018/11/28
 */
public class TopicEventPublisher implements InitializingBean, ConfigurationListener, ApplicationEventPublisherAware, SmartInitializingSingleton {
    private ApplicationEventPublisher applicationEventPublisher;
    private ApplicationConfiguration applicationConfiguration;

    @Override
    public void notifyInit(Configuration configuration) {
        //TODO ignore
    }

    @Override
    public void notifyUpdate(Configuration configuration, String key) {
        if (ConfigKeys.GO_KAFKA_CONSUMER_TOPICS.equals(key)) {
            publishEvent();
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        publishEvent();
    }

    private void publishEvent() {
        String topicPattern = applicationConfiguration.getString(ConfigKeys.GO_KAFKA_CONSUMER_TOPICS);
        List<String> topics = new ArrayList<>();
        if (StringUtils.isNotBlank(topicPattern)) {
            topics = parse(topicPattern);
        }
        applicationEventPublisher.publishEvent(new TopicEvent(applicationConfiguration, ConfigKeys.GO_KAFKA_CONSUMER_TOPICS, topics));
    }

    private List<String> parse(String text) {
        return Arrays.stream(text.split(",")).collect(Collectors.toList());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() {
        this.applicationConfiguration.addListener(this);
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }
}
