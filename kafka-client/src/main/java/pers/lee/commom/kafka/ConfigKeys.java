package pers.lee.commom.kafka;

/**
 * @author: Jay.Lee
 * @date: 2018/11/27
 */
public interface ConfigKeys {
    String GO_KAFKA_CONSUMER_TOPICS = "go.kafka.consumer.topics";
    String GO_KAFKA_CONSUMER_AUTO_STARTUP = "go.kafka.consumer.auto-startup";
    String GO_KAFKA_CONSUMER_EXCLUDE_CONTRACT_KEYS = "go.kafka.consumer.exclude.contract.keys";
    String GO_KAFKA_CONTAINER_CHECKER_SCHEDULER_ENABLE = "go.kafka.container.checker.scheduler.enable";
    String GO_KAFKA_PRODUCER_TRANSACTION_ID_PREFIX = "go.kafka.producer.transaction-id-prefix";
}
