package pers.lee.common.rpc.ci.status;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * C3P0Status
 *
 * @author Drizzt Yang
 */
public class DruidStatus implements StatusAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(DruidStatus.class);
    public static final String NAME = "druid";
    private static final Set<String> KEYS = new HashSet<String>();

    private static DruidStatManagerFacade statManagerFacade = DruidStatManagerFacade.getInstance();

    static {
        KEYS.add("connections.InitialSize");
        KEYS.add("connections.MinIdle");
        KEYS.add("connections.MaxActive");
        KEYS.add("connections.QueryTimeout");
        KEYS.add("connections.TransactionQueryTimeout");
        KEYS.add("connections.NotEmptyWaitCount");
        KEYS.add("connections.NotEmptyWaitMillis");
        KEYS.add("connections.WaitThreadCount");
        KEYS.add("connections.StartTransactionCount");
        KEYS.add("connections.TransactionHistogram");
        KEYS.add("connections.NotEmptyWaitCount");
        KEYS.add("connections.PoolingCount");
        KEYS.add("connections.PoolingPeak");
        KEYS.add("connections.PoolingPeakTime");
        KEYS.add("connections.ActiveCount");
        KEYS.add("connections.ActivePeak");
        KEYS.add("connections.ActivePeakTime");
        KEYS.add("connections.LogicConnectCount");
        KEYS.add("connections.LogicCloseCount");
        KEYS.add("connections.LogicConnectErrorCount");
        KEYS.add("connections.PhysicalConnectCount");
        KEYS.add("connections.PhysicalCloseCount");
        KEYS.add("connections.PhysicalConnectErrorCount");
        KEYS.add("connections.ExecuteCount");
        KEYS.add("connections.ErrorCount");
        KEYS.add("connections.CommitCount");
        KEYS.add("connections.RollbackCount");
        KEYS.add("connections.PSCacheAccessCount");
        KEYS.add("connections.PSCacheHitCount");
        KEYS.add("connections.PSCacheMissCount");
        KEYS.add("connections.ConnectionHoldTimeHistogram");
        KEYS.add("connections.ClobOpenCount");
        KEYS.add("connections.BlobOpenCount");
    }

    @Override
    public String getStatusPrefix() {
        return NAME;
    }

    @Override
    public Set<String> getStatusKeys() {
        return KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();

        List<Map<String, Object>> dataSources = statManagerFacade.getDataSourceStatDataList();
        if (dataSources != null) {
            Map<String, Object> dataSourceStatus = dataSources.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            for (String key : KEYS) {
                Object value = dataSourceStatus.get(key.substring("connections.".length()));
                if (value instanceof Date) {
                    value = sdf.format((Date) value);
                }

                if (value != null && value.getClass().isArray()) {
                    value = ArrayUtils.toString(value);
                }
                status.put(key, String.valueOf(value));
            }
        }
        return status;
    }
}
