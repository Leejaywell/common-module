package pers.lee.commom.kafka.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Passyt on 2018/12/17.
 */
public abstract class AbstractDeserializer<T> implements Deserializer<T> {

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        //nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }
}
