package pers.lee.common.rpc.ci.status;

import java.util.HashMap;
import java.util.Map;

/**
 * PeriodCountStat
 *
 * @author Drizzt Yang
 */
public class PeriodCountStat implements PeriodStat<Object> {
    private long periodKey;
    private long periodLength = 0L;
    private Integer count;
    private Map<Object, Integer> countMap;

    public PeriodCountStat() {
        count = 0;
        countMap = new HashMap<Object, Integer>();
    }

    public PeriodCountStat(long periodLength) {
        this.periodLength = periodLength;
    }

    private void rolling(long timestamp) {
        periodKey = timestamp / periodLength;
        count = 0;
        countMap = new HashMap<Object, Integer>();
    }

    @Override
    public synchronized void accept(long timestamp, Object object) {
        if (periodLength > 0) {
            long key = timestamp / periodLength;
            if (key < periodKey) {
                return;
            } else if (key > periodKey) {
                rolling(timestamp);
            }
        }

        count++;

        if (countMap.get(object) == null) {
            countMap.put(object, 0);
        }
        countMap.put(object, countMap.get(object) + 1);
    }

    public Integer getCount() {
        return count;
    }

    public Map<Object, Integer> getCountMap() {
        return countMap;
    }

    public long getPeriodKey() {
        return periodKey;
    }

    public long getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(long periodLength) {
        this.periodLength = periodLength;
    }
}
