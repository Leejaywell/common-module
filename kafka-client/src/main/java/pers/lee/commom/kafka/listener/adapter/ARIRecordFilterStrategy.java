package pers.lee.commom.kafka.listener.adapter;

import pers.lee.commom.kafka.ConfigKeys;
import pers.lee.commom.kafka.model.Contract;
import pers.lee.common.config.Config;
import pers.lee.common.lang.json.Json;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author: Jay.Lee
 * @date: 2019/3/15
 */
public class ARIRecordFilterStrategy implements RecordFilterStrategy<Object, Object> {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Set<Contract> excludeKeys = new HashSet<>();

    @Override
    public boolean filter(ConsumerRecord<Object, Object> record) {
        if (record.key() instanceof Contract && !excludeKeys.isEmpty()) {
            Predicate<Contract> contractPredicate = (contract) -> contract.getDistributorId().equals(((Contract) record.key()).getDistributorId())
                    && contract.getSourceId().equals(((Contract) record.key()).getSourceId());
            if (excludeKeys.stream().anyMatch(contractPredicate)) {
                log.info("Remove message:Contract[{}],Partition[{}],Offset[{}] ", record.key().toString(), record.partition(), record.offset());
                return true;
            }
        }
        return false;
    }

    @Config(ConfigKeys.GO_KAFKA_CONSUMER_EXCLUDE_CONTRACT_KEYS)
    public void setExcludeKeys(String json) {
        try {
            this.excludeKeys = Optional.ofNullable(Json.getDefault().toSet(json, Contract.class)).orElse(new HashSet<>());
        } catch (Exception e) {
            log.error("Invalid arguments");
            this.excludeKeys = new HashSet<>();
        }
    }
}
