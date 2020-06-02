package pers.lee.common.rpc.ci.status;

/**
 * PeriodDurationStat
 *
 * @author Drizzt Yang
 */
public class PeriodDurationStat implements PeriodStat<Long> {
    private long periodKey;
    private long periodLength = 0L;
    private Long totalDuration = 0L;
    private Long minDuration = 0L;
    private Long maxDuration = 0L;
    private Integer count = 0;

    public PeriodDurationStat() {
        count = 0;
        totalDuration = 0L;
        minDuration = 0L;
        maxDuration = 0L;
    }

    public PeriodDurationStat(long periodLength) {
        this.periodLength = periodLength;
    }

    private void rolling(long timestamp) {
        periodKey = timestamp / periodLength;
        count = 0;
        totalDuration = 0L;
        minDuration = 0L;
        maxDuration = 0L;
    }

    @Override
    public void accept(long timestamp, Long duration) {
        if (periodLength > 0) {
            long key = timestamp / periodLength;
            if (key < periodKey) {
                return;
            } else if (key > periodKey) {
                rolling(timestamp);
            }
        }

        count++;
        totalDuration = totalDuration + duration;
        if(minDuration > duration || minDuration == 0L) {
            minDuration = duration;
        }
        if(maxDuration < duration) {
            maxDuration = duration;
        }
    }

    public Integer getCount() {
        return count;
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

    public Long getTotalDuration() {
        return totalDuration;
    }
    
    public Long getMinDuration() {
        return minDuration;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }
}
