package pers.lee.common.lang.concurrent;

import java.util.concurrent.*;

/**
 * SimplePoolExecutor
 *
 * @author Drizzt Yang
 */
public class SimplePoolExecutor extends ThreadPoolExecutor {

    public SimplePoolExecutor(int poolSize, ThreadFactory threadFactory) {
        this(poolSize, new LinkedBlockingDeque<Runnable>(), threadFactory);
    }

    public SimplePoolExecutor(int poolSize, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(poolSize, poolSize, 0L, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    public void setPoolSize(int poolSize) {
        this.setCorePoolSize(poolSize);
        super.setMaximumPoolSize(poolSize);
    }
}
