package pers.lee.common.lang.concurrent;

import java.util.concurrent.Callable;

/**
 * 
 * @author Norther
 * 
 */
public interface ISynchronizationExecutor<O> {

	/**
	 * invoke callable with synchronized by parameter
	 * 
	 * @param <T>
	 * @param callable
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	<T> T synchronizedExecute(Callable<T> callable, O parameter) throws Exception;

	/**
	 * return the total number of locks
	 * 
	 * @return
	 */
	Integer getLockCount();

}
