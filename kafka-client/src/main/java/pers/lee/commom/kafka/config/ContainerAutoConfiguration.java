package pers.lee.commom.kafka.config;

import pers.lee.commom.kafka.ConfigKeys;
import pers.lee.commom.kafka.admin.KafkaContainerMessageListenerChecker;
import pers.lee.commom.kafka.admin.KafkaMessageListenerContainerAdmin;
import pers.lee.commom.kafka.listener.LoggingBatchErrorHandler;
import pers.lee.commom.kafka.listener.adapter.ARIRecordFilterStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.GenericMessageListener;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;

/**
 * @author: Jay.Lee
 * @date: 2019/3/19
 */
@ConditionalOnBean(value = {GenericMessageListener.class})
public class ContainerAutoConfiguration {
    private final KafkaProperties properties;
    private final RecordMessageConverter messageConverter;
    private final AfterRollbackProcessor<Object, Object> afterRollbackProcessor;
    private final BatchMessageListener genericMessageListener;
    private final KafkaTransactionManager kafkaTransactionManager;

    public ContainerAutoConfiguration(KafkaProperties properties,
                                      ObjectProvider<RecordMessageConverter> messageConverter,
                                      ObjectProvider<BatchMessageListener> batchMessageListeners,
                                      ObjectProvider<KafkaTransactionManager> kafkaTransactionManager,
                                      ObjectProvider<AfterRollbackProcessor<Object, Object>> afterRollbackProcessor) {
        this.properties = properties;
        this.kafkaTransactionManager = kafkaTransactionManager.getIfUnique();
        this.messageConverter = messageConverter.getIfUnique();
        this.afterRollbackProcessor = afterRollbackProcessor.getIfUnique();
        this.genericMessageListener = batchMessageListeners.getIfUnique();
        if (this.genericMessageListener == null) {
            throw new IllegalStateException("GenericMessageListener bean is required!");
        }
    }

    @Bean
    public KafkaContainerMessageListenerChecker kafkaContainerMessageListenerCheck(KafkaMessageListenerContainerAdmin admin) {
        return new KafkaContainerMessageListenerChecker(admin);
    }

    @Bean
    public KafkaMessageListenerContainerAdmin kafkaContainerMonitor(KafkaMessageListenerContainerRegistry kafkaMessageListenerContainerRegistry) {
        return new KafkaMessageListenerContainerAdmin(kafkaMessageListenerContainerRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public RecordFilterStrategy<?, ?> recordFilterStrategy() {
        return new ARIRecordFilterStrategy();
    }

    @Bean
    @ConditionalOnMissingBean
    public BatchErrorHandler containerErrorHandler() {
        return new LoggingBatchErrorHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConcurrentKafkaListenerContainerFactoryConfigurer concurrentKafkaListenerContainerFactoryConfigurer(
            RecordFilterStrategy recordFilterStrategy, BatchErrorHandler batchErrorHandler) {
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer = new ConcurrentKafkaListenerContainerFactoryConfigurer();
        configurer.setKafkaProperties(this.properties);
        configurer.setMessageConverter(this.messageConverter);
//        configurer.setReplyTemplate(this.kafkaTemplate);
        configurer.setTransactionManager(this.kafkaTransactionManager);
        configurer.setBatchErrorHandler(batchErrorHandler);
        configurer.setAfterRollbackProcessor(this.afterRollbackProcessor);
        configurer.setBatchMessageListener(this.genericMessageListener);
        configurer.setRecordFilterStrategy(recordFilterStrategy);
        return configurer;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConcurrentKafkaListenerContainerFactory<?, ?> concurrentKafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
                                                                                                 ConsumerFactory<Object, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(value = KafkaListenerEndpointRegistry.class, name = KafkaListenerConfigUtils.KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    @Bean
    public KafkaMessageListenerContainerRegistry kafkaMessageListenerContainerRegistry(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                                                                       ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory) {
        return new KafkaMessageListenerContainerRegistry(kafkaListenerEndpointRegistry, concurrentKafkaListenerContainerFactory);
    }

    @Bean
    @ConditionalOnProperty(name = ConfigKeys.GO_KAFKA_CONSUMER_TOPICS)
    @ConditionalOnBean(KafkaMessageListenerContainerRegistry.class)
    public KafkaMessageListenerContainerRegistrar kafkaMessageListenerContainerRegistrar(KafkaMessageListenerContainerRegistry registry) {
        return new KafkaMessageListenerContainerRegistrar(properties, registry);
    }

    @Bean
    @ConditionalOnProperty(name = ConfigKeys.GO_KAFKA_CONSUMER_TOPICS)
    @ConditionalOnBean(KafkaMessageListenerContainerRegistrar.class)
    public KafkaContainerRegistrar kafkaContainerRegistrar(KafkaMessageListenerContainerRegistrar registrar) {
        return new KafkaContainerRegistrar(registrar);
    }

}
