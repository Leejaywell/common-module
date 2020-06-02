package pers.lee.commom.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Jay.Lee
 * @date: 2018/11/26
 */
@Configuration
public class KafkaMarkerConfiguration {

    @Bean
    public Marker kafkaMarker() {
        return new Marker();
    }

    public static class Marker {

    }
}
