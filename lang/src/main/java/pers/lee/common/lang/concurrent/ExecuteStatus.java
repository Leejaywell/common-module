package pers.lee.common.lang.concurrent;

/**
 * The status of task
 * 
 * @author Passyt
 * 
 */
public enum ExecuteStatus {

	Wait(true), Executing(true), Finish(false), Cancel(false), Error(false), Lock(true);

	private boolean isRunning;

	private ExecuteStatus(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isRunning() {
		return isRunning;
	}

}
