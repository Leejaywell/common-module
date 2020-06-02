/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pers.lee.commom.kafka.config;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.adapter.FilteringBatchMessageListenerAdapter;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;

/**
 * Configure {@link ConcurrentKafkaListenerContainerFactory} with sensible defaults.
 *
 * @author Gary Russell
 * @author Eddú Meléndez
 * @since 1.5.0
 */
public class ConcurrentKafkaListenerContainerFactoryConfigurer {

    private KafkaProperties properties;

    private RecordMessageConverter messageConverter;

    private RetryTemplate retryTemplate;

    private KafkaTransactionManager<Object, Object> transactionManager;

    private ErrorHandler errorHandler;

    private BatchErrorHandler batchErrorHandler;

    private AfterRollbackProcessor<Object, Object> afterRollbackProcessor;

    private BatchMessageListener batchMessageListener;

    private RecordFilterStrategy recordFilterStrategy;

    /**
     * Set the {@link KafkaProperties} to use.
     *
     * @param properties the properties
     */
    public void setKafkaProperties(KafkaProperties properties) {
        this.properties = properties;
    }

    /**
     * Set the {@link RecordMessageConverter} to use.
     *
     * @param messageConverter the message converter
     */
    public void setMessageConverter(RecordMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /**
     * Set the {@link RetryTemplate} to use to send replies.
     *
     * @param replyTemplate the reply template
     */
    public void setReplyTemplate(RetryTemplate replyTemplate) {
        this.retryTemplate = retryTemplate;
    }

    /**
     * Set the {@link KafkaTransactionManager} to use.
     *
     * @param transactionManager the transaction manager
     */
    public void setTransactionManager(
            KafkaTransactionManager<Object, Object> transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Set the {@link ErrorHandler} to use.
     *
     * @param errorHandler the error handler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setBatchErrorHandler(BatchErrorHandler batchErrorHandler) {
        this.batchErrorHandler = batchErrorHandler;
    }

    /**
     * Set the {@link AfterRollbackProcessor} to use.
     *
     * @param afterRollbackProcessor the after rollback processor
     */
    public void setAfterRollbackProcessor(
            AfterRollbackProcessor<Object, Object> afterRollbackProcessor) {
        this.afterRollbackProcessor = afterRollbackProcessor;
    }

    public void setBatchMessageListener(BatchMessageListener batchMessageListener) {
        this.batchMessageListener = batchMessageListener;
    }

    public void setRecordFilterStrategy(RecordFilterStrategy recordFilterStrategy) {
        this.recordFilterStrategy = recordFilterStrategy;
    }

    /**
     * Configure the specified Kafka listener container factory. The factory can be
     * further tuned and default settings can be overridden.
     *
     * @param listenerFactory the {@link ConcurrentKafkaListenerContainerFactory} instance
     *                        to configure
     * @param consumerFactory the {@link ConsumerFactory} to use
     */
    public void configure(
            ConcurrentKafkaListenerContainerFactory<Object, Object> listenerFactory,
            ConsumerFactory<Object, Object> consumerFactory) {
        listenerFactory.setConsumerFactory(consumerFactory);
        configureListenerFactory(listenerFactory);
        configureContainer(listenerFactory.getContainerProperties());
    }

    private void configureListenerFactory(
            ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        map.from(properties::getConcurrency).to(factory::setConcurrency);
        map.from(this.messageConverter).to(factory::setMessageConverter);
        map.from(this.retryTemplate).to(factory::setRetryTemplate);
        map.from(properties::getType).whenEqualTo(KafkaProperties.Listener.Type.BATCH)
                .toCall(() -> factory.setBatchListener(true));
        map.from(this.afterRollbackProcessor).to(factory::setAfterRollbackProcessor);
    }

    private void configureContainer(ContainerProperties container) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        map.from(properties::getAckMode).to(container::setAckMode);
//        map.from(properties::getClientId).to(container::setClientId);
        map.from(properties::getAckCount).to(container::setAckCount);
        map.from(properties::getAckTime).as(Duration::toMillis).to(container::setAckTime);
        map.from(properties::getPollTimeout).as(Duration::toMillis)
                .to(container::setPollTimeout);
        map.from(properties::getNoPollThreshold).to(container::setNoPollThreshold);
        map.from(properties::getIdleEventInterval).as(Duration::toMillis)
                .to(container::setIdleEventInterval);
        map.from(properties::getMonitorInterval).as(Duration::getSeconds)
                .as(Number::intValue).to(container::setMonitorInterval);
        map.from(this.errorHandler).to(container::setErrorHandler);
        map.from(this.batchErrorHandler).to(container::setGenericErrorHandler);
        map.from(this.transactionManager).to(container::setTransactionManager);
        if (this.recordFilterStrategy != null) {
            map.from(new FilteringBatchMessageListenerAdapter(this.batchMessageListener, this.recordFilterStrategy)).to(container::setMessageListener);
        } else {
            map.from(this.batchMessageListener).to(container::setMessageListener);
        }
    }

}
