package pers.lee.common.rpc.ci.status;

/**
 * PeriodStat
 *
 * @author Drizzt Yang
 */
public interface PeriodStat<T> {
    void accept(long timestamp, T value);
}
