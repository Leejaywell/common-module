package pers.lee.commom.kafka.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import pers.lee.commom.kafka.ConfigKeys;
import pers.lee.commom.kafka.listener.DefaultProducerListener;
import pers.lee.commom.kafka.listener.TopicEventListener;
import pers.lee.commom.kafka.listener.TopicEventPublisher;
import pers.lee.common.config.ApplicationConfiguration;

/**
 * Add the following code in main class
 * @SpringBootApplication(exclude = org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class)
 */
@Configuration
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnBean(KafkaMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(KafkaProperties.class)
@Import(ContainerAutoConfiguration.class)
public class KafkaAutoConfiguration {
    private KafkaProperties properties;
    private final RecordMessageConverter messageConverter;

    public KafkaAutoConfiguration(KafkaProperties properties,
                                  ObjectProvider<RecordMessageConverter> messageConverter) {
        this.properties = properties;
        this.messageConverter = messageConverter.getIfUnique();

    }

    @Bean
    @ConditionalOnMissingBean
    public ProducerListener<?, ?> kafkaProducerListener() {
        return new DefaultProducerListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaAdmin kafkaAdmin() {
        KafkaAdmin kafkaAdmin = new KafkaAdmin(this.properties.buildAdminProperties());
        kafkaAdmin.setFatalIfBrokerNotAvailable(this.properties.getAdmin().isFailFast());
        return kafkaAdmin;
    }

    @Bean
    @ConditionalOnMissingBean(KafkaTemplate.class)
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
                                             ProducerListener<Object, Object> kafkaProducerListener) {
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(
                kafkaProducerFactory);
        if (this.messageConverter != null) {
            kafkaTemplate.setMessageConverter(this.messageConverter);
        }
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(this.properties.getTemplate().getDefaultTopic());
        return kafkaTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerFactory.class)
    public ConsumerFactory<?, ?> kafkaConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                this.properties.buildConsumerProperties());
    }

    @Bean
    @ConditionalOnMissingBean(ProducerFactory.class)
    public ProducerFactory<?, ?> kafkaProducerFactory() {
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
                this.properties.buildProducerProperties());
        String transactionIdPrefix = this.properties.getProducer()
                .getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        return factory;
    }

    @Bean
    @ConditionalOnProperty(name = ConfigKeys.GO_KAFKA_PRODUCER_TRANSACTION_ID_PREFIX)
    @ConditionalOnMissingBean
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public TopicEventPublisher kafkaTopicEventPublisher(ApplicationConfiguration configuration) {
        TopicEventPublisher topicEventPublisher = new TopicEventPublisher();
        topicEventPublisher.setApplicationConfiguration(configuration);
        return topicEventPublisher;
    }

    @Bean
    @ConditionalOnBean(KafkaMessageListenerContainerRegistrar.class)
    @ConditionalOnProperty(name = ConfigKeys.GO_KAFKA_CONSUMER_AUTO_STARTUP, havingValue = "true", matchIfMissing = false)
    public TopicEventListener kafkaTopicEventListener(KafkaMessageListenerContainerRegistrar kafkaMessageListenerContainerRegistrar) {
        return new TopicEventListener(kafkaMessageListenerContainerRegistrar);
    }

}
