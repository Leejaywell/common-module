package pers.lee.common.lang.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author Norther
 * 
 */
public class SynchronizationExecutor<O> implements ISynchronizationExecutor<O> {

	private final Map<Object, Lock> locks = new HashMap<Object, Lock>();
	private final Lock lock = new ReentrantLock();

	@Override
	public <T> T synchronizedExecute(Callable<T> callable, O parameter) throws Exception {
		if (callable == null) {
			throw new IllegalArgumentException("The argument 'callable' is required");
		}
		if (parameter == null) {
			throw new IllegalArgumentException("The argument 'parameter' is required");
		}

		final Lock lock = getLock(parameter);
		lock.lock();
		try {
			return callable.call();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Integer getLockCount() {
		return locks.size();
	}

	private Lock getLock(O parameter) {
		Object lockTarget = getLockTarget(parameter);
		final Lock mainLock = this.lock;
		mainLock.lock();
		try {
			Lock lock = locks.get(lockTarget);
			if (lock == null) {
				lock = new ReentrantLock();
				locks.put(lockTarget, lock);
			}

			return lock;
		} finally {
			mainLock.unlock();
		}
	}

	protected Object getLockTarget(O parameter) {
		return parameter;
	}

}
