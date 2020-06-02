package pers.lee.common.lang.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author Norther
 */
public class RetryTemplate {

    private static final Logger log = LoggerFactory.getLogger(RetryTemplate.class);

    public <T> T doWithRetry(Callable<T> callable, int retryTimes) throws Throwable {
        return doWithRetry(callable, retryTimes, 0);
    }

    public <T> T doWithRetry(Callable<T> callable, int retryTimes, int retryInterval) throws Throwable {
        if (retryTimes < 0) {
            throw new IllegalArgumentException("the argument 'retryTimes' cannot less than 0");
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("Call");
            }
            T returnObject = callable.call();
            if (log.isDebugEnabled()) {
                log.debug("Successful");
            }
            return returnObject;
        } catch (Throwable throwable) {
            if (log.isDebugEnabled()) {
                log.debug("Caught throwable : " + throwable.getClass());
            }
            if (retryTimes != 0) {
                if (canRetry(throwable)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Retry! the chance of retry " + (retryTimes));
                    }
                    if (retryInterval > 0) {
                        TimeUnit.SECONDS.sleep(retryInterval);
                    }
                    return doWithRetry(callable, retryTimes - 1, retryInterval);
                }
            }
            throw throwable;
        }
    }

    protected boolean canRetry(Throwable throwable) {
        return true;
    }

}
