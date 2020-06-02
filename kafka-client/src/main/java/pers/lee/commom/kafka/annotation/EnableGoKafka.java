package pers.lee.commom.kafka.annotation;

import pers.lee.commom.kafka.config.KafkaMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: Jay.Lee
 * @date: 2018/11/26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({KafkaMarkerConfiguration.class})
public @interface EnableGoKafka {

    /**
     * message type of consumer
     * @return
     */
    MessageType messageType() default MessageType.CUSTOM;
}
