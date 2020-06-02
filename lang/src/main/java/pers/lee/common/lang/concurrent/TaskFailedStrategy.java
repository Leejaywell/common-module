package pers.lee.common.lang.concurrent;

/**
 * 
 * @author Passyt
 * 
 */
public interface TaskFailedStrategy {

	boolean isFailed(Throwable throwable);

	<Key, Result> Exception toException(Task<Key, Result> task, Exception exception);

}
