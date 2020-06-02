package pers.lee.common.lang.concurrent;

/**
 * 
 * @author Passyt
 * 
 */
public interface TaskRemoveStrategy {

	<Key> void removeTask(TaskExecuteManager<Key> taskExecuteManager, Key key, ExecuteStatus taskStatus);

	void shutdown();

}
