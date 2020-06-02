package pers.lee.common.lang.concurrent;

import pers.lee.common.lang.concurrent.Task.TaskStatus;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * A task execute manager could manage all the tasks.
 * 
 * @author Passyt
 * 
 */
public interface TaskExecuteManager<Key> {

	/**
	 * Submit a task to the task manager
	 * 
	 * @param <Result>
	 *            the result of executing the task
	 * @param task
	 *            the task
	 * @return the future
	 */
	<Result> Future<Result> submit(Task<Key, Result> task);

	/**
	 * 
	 * @param <Result>
	 *            the result of executing the task
	 * @param tasks
	 * @return
	 */
	<Result> List<Future<Result>> invokeAll(Collection<? extends Task<Key, Result>> tasks);

	/**
	 * Cancel a task
	 * 
	 * @param key
	 * @return
	 */
	boolean cancel(Key key);

	/**
	 * Remove a task from the task status
	 * 
	 * @param key
	 */
	boolean remove(Key key);
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	TaskStatus<Key, ?> getTaskStatus(Key key);

	/**
	 * 
	 * @return all the status of tasks
	 */
	Collection<? extends TaskStatus<Key, ?>> getTasksStatus();

	/**
	 * Shut down the task manager
	 */
	void shutdown();

}
