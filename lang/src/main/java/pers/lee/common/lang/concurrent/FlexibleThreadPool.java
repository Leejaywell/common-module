package pers.lee.common.lang.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Passyt on 2017/5/18.
 */
public class FlexibleThreadPool extends ThreadPoolExecutor {

    private int minimalPoolSize;
    private AtomicInteger count = new AtomicInteger();

    /**
     * @param minimalPoolSize minimal thread size in pool
     * @param maximumPoolSize maximum thread size in pool
     * @param threadFactory
     */
    public FlexibleThreadPool(int minimalPoolSize, int maximumPoolSize, ThreadFactory threadFactory) {
        super(minimalPoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
        this.minimalPoolSize = minimalPoolSize;
    }

    @Override
    public void execute(Runnable command) {
        adjustPoolSize(count.incrementAndGet());
        super.execute(command);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        adjustPoolSize(count.decrementAndGet());
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (getCorePoolSize() > maximumPoolSize) {
            setCorePoolSize(maximumPoolSize);
        }
        super.setMaximumPoolSize(maximumPoolSize);
    }

    private void adjustPoolSize(int poolSize) {
        setCorePoolSize(Math.min(Math.max(minimalPoolSize, poolSize), getMaximumPoolSize()));
    }

}
