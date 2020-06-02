package pers.lee.commom.kafka.serializer;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Passyt on 2018/12/17.
 */
public abstract class AbstractSerializer<T> implements Serializer<T> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

}
