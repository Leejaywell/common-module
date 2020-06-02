package pers.lee.commom.kafka.listener;

import pers.lee.commom.kafka.model.Contract;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;

/**
 * @author: Jay.Lee
 * @date: 2018/11/26
 */
public class DefaultProducerListener implements ProducerListener<Contract, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger("http.StreamLog");

    @Override
    public void onSuccess(String topic, Integer partition, Contract key, Object value, RecordMetadata recordMetadata) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Message from topic:[{}], source[{}], distributor[{}], hotel[{}] and record meta data [partition:{},offset:{},serializedKeySize:{},serializedValueSize:{}] send successfully",
                    topic, key.getSourceId(), key.getDistributorId(), key.getHotelId(), recordMetadata.partition(),
                    recordMetadata.offset(), recordMetadata.serializedKeySize(), recordMetadata.serializedValueSize());
        }
    }

    @Override
    public void onError(String topic, Integer partition, Contract key, Object value, Exception exception) {
        LOGGER.error("Message from topic:[{" + topic + "}], source[{" + key.getSourceId() + "}], distributor[{" + key.getDistributorId() + "}], hotel[{" + key.getHotelId() + "}] send failed", exception);
    }

    @Override
    public boolean isInterestedInSuccess() {
        return true;
    }
}
