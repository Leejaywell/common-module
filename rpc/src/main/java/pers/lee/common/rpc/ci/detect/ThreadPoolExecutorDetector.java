package pers.lee.common.rpc.ci.detect;

import com.google.common.base.Throwables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * ThreadPoolExecutorDetector
 *
 * @author Drizzt Yang
 */
public class ThreadPoolExecutorDetector extends DetectorBase implements Detector {
    private ThreadPoolExecutor executorService;
    private int timeoutSeconds;

    @Override
    public DetectResult detect() {
        DetectCallable detectCallable = new DetectCallable();
        boolean result;
        List<Runnable> buffer = new ArrayList<Runnable>();
        int corePoolSize = executorService.getCorePoolSize();

        try {
            BlockingQueue<Runnable> queue = executorService.getQueue();
            while (queue.peek() != null) {
                buffer.add(queue.take());
            }
            executorService.setCorePoolSize(corePoolSize + 1);

            Future<Boolean> future = executorService.submit(detectCallable);
            result = future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            return DetectResult.fail(this.getDetectorName(), Throwables.getStackTraceAsString(e));
        } finally {
            for (Runnable runnable : buffer) {
                executorService.submit(runnable);    //TODO: it may change the future, need to test
            }
            executorService.setCorePoolSize(corePoolSize);
        }
        if (result) {
            return DetectResult.pass(this.getDetectorName(), "");
        } else {
            return DetectResult.fail(this.getDetectorName(), "");
        }
    }

    public void setExecutorService(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    static class DetectCallable implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            return true;
        }
    }
}
