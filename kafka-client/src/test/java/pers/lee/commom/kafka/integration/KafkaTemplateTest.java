package pers.lee.commom.kafka.integration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: Jay
 * @date: 2018/3/23
 */
public class KafkaTemplateTest {
    private static final String UAT_SERVERS = "52.88.185.147:9092,34.208.215.36:9092,52.25.23.134:9092";
    private static final String LOCAL_SERVERS = "10.200.170.9:9092";
    private static final String QA_SERVERS = "52.197.253.82:9092";

    @Test
    public void test() throws InterruptedException {
        DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs());
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate(producerFactory);
        while (true) {
            int i = new Random().nextInt(10);
            ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(new ProducerRecord<>("test", String.valueOf(i), "test" + i));
            listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("send failed!!!!");
                }

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    String log = String.format("send message key [%s], value[%s]", result.getProducerRecord().key(), result.getProducerRecord().value());
                    System.out.println(log);
                }
            });
            TimeUnit.SECONDS.sleep(3);
        }
    }

    private Map producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, UAT_SERVERS);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", StringSerializer.class);
        return props;
    }
}
