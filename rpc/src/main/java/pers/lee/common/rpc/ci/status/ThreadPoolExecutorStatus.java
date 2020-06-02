package pers.lee.common.rpc.ci.status;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadPoolExecutorStatus
 *
 * @author Drizzt Yang
 */
public class ThreadPoolExecutorStatus implements StatusAware {
    private ThreadPoolExecutor threadPoolExecutor;
    
    private String prefix;

    public ThreadPoolExecutorStatus(ThreadPoolExecutor scheduledThreadPoolExecutor, String prefix) {
        this.prefix = prefix;
        this.threadPoolExecutor = scheduledThreadPoolExecutor;
    }

    @Override
    public String getStatusPrefix() {
        return prefix;
    }

    @Override
    public Set<String> getStatusKeys() {
        Set<String> keys = new HashSet<String>();
        keys.add("complete");
        keys.add("waiting");
        keys.add("total");
        keys.add("current.max");
        keys.add("current.active");
        return keys;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> status = new LinkedHashMap<String, String>();

        status.put("complete", String.valueOf(threadPoolExecutor.getCompletedTaskCount()));
        status.put("waiting", String.valueOf(threadPoolExecutor.getQueue().size()));
        status.put("total", String.valueOf(threadPoolExecutor.getTaskCount()));
        status.put("current.max", String.valueOf(threadPoolExecutor.getCorePoolSize()));
        status.put("current.active", String.valueOf(threadPoolExecutor.getActiveCount()));
        return status;
    }
}
