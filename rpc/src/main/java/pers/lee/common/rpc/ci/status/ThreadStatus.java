package pers.lee.common.rpc.ci.status;


import pers.lee.common.lang.properties.SummaryMap;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * ThreadStatus
 *
 * @author Drizzt Yang
 */
public class ThreadStatus implements StatusAware {

    public static final String THREAD = "thread";
    public static final Set<String> THREAD_KEYS = new HashSet<String>();
    static {
        THREAD_KEYS.add("total");
        THREAD_KEYS.add("state.");
        THREAD_KEYS.add("detail.");
    }

    @Override
    public String getStatusPrefix() {
        return THREAD;
    }

    @Override
    public Set<String> getStatusKeys() {
        return THREAD_KEYS;
    }

    @Override
    public Map<String, String> status() {
        Map<String, String> threadStatus = new LinkedHashMap<String, String>();

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[threadGroup.activeCount()];
        int numThreads = threadGroup.enumerate(threads);

        threadStatus.put("total", String.valueOf(numThreads));
        SummaryMap stateSummaries = new SummaryMap();
        for (int i = 0; i < numThreads; i++) {
            long threadId = threads[i].getId();
            threadStatus.put("detail." + threadId + ".name", threads[i].getName());
            threadStatus.put("detail." + threadId + ".priority", String.valueOf(threads[i].getPriority()));
            threadStatus.put("detail." + threadId + ".daemon", String.valueOf(threads[i].isDaemon()));
            
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
            if (threadInfo == null) {
                continue;
            }
            if (threadInfo.getThreadState() != null) {
                threadStatus.put("detail." + threadId + ".state", threadInfo.getThreadState().name());
                stateSummaries.addKeyCount(threadInfo.getThreadState().name());
            }
        }
        for (Map.Entry<Object, Integer> entry : stateSummaries.entrySet()) {
            threadStatus.put("state." + entry.getKey().toString(), String.valueOf(entry.getValue()));
        }

        return threadStatus;
    }

    private static ThreadStatus threadStatus = new ThreadStatus();

    public static ThreadStatus get() {
        return threadStatus;
    }
}
