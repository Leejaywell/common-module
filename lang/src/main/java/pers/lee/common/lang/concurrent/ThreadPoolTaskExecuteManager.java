package pers.lee.common.lang.concurrent;

import pers.lee.common.lang.concurrent.Task.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * A implement of {@link TaskExecuteManager}, which using
 * <tt>ThreadPoolExecutor<tt> to manage all the runnable.The
 * priority of runnable is reference to {@link Task#getPriority()}
 *
 * @author Passyt
 * @see TaskExecuteManager
 */
public class ThreadPoolTaskExecuteManager<Key> implements TaskExecuteManager<Key> {

    protected static final Logger log = LoggerFactory.getLogger(TaskExecuteManager.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    protected final ConcurrentMap<Key, TaskStatusCallable<Key, ?>> tasks = new ConcurrentHashMap<>();

    protected TaskFailedStrategy taskFailedStrategy = new DefaultTaskFailedStrategy();
    protected Exception2Message exception2Message = new DefaultException2Message();
    protected TaskRemoveStrategy taskRemoveStrategy = new FinishTaskDelayRemoveStrategy();
    protected ISynchronizationExecutor<Key> synchronizationExecutor;

    public ThreadPoolTaskExecuteManager(int threadSize) {
        this(threadSize, Executors.defaultThreadFactory());
    }

    public ThreadPoolTaskExecuteManager(int threadSize, ThreadFactory threadFactory) {
        threadPoolExecutor = new PriorityThreadPoolExecutor(threadSize, threadSize, 0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(), threadFactory);
    }

    @Override
    public <Result> Future<Result> submit(Task<Key, Result> task) {
        if (!isExecutable(task)) {
            return null;
        }

        TaskStatusCallable<Key, Result> callable = createTaskStatusCallable(task);
        tasks.put(task.getKey(), callable);

        Future<Result> future = threadPoolExecutor.submit(callable);
        callable.setFuture(future);
        return future;
    }

    @Override
    public <Result> List<Future<Result>> invokeAll(Collection<? extends Task<Key, Result>> tasks) {
        List<TaskStatusCallable<Key, Result>> callables = new ArrayList<>();
        for (Task<Key, Result> task : tasks) {
            if (isExecutable(task)) {
                TaskStatusCallable<Key, Result> callable = createTaskStatusCallable(task);
                this.tasks.put(task.getKey(), callable);
                callables.add(callable);
            }
        }

        try {
            List<Future<Result>> futures = threadPoolExecutor.invokeAll(callables);
            return futures;
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private <Result> TaskStatusCallable<Key, Result> createTaskStatusCallable(Task<Key, Result> task) {
        return new TaskStatusCallable<Key, Result>(task, new Callback<Key, Result>() {

            @Override
            public void invoke(TaskStatusCallable<Key, Result> callable) {
                finishCallable(callable);
            }

        }, taskFailedStrategy, exception2Message, synchronizationExecutor);
    }

    protected <Result> boolean isExecutable(Task<Key, Result> task) {
        TaskStatusCallable<Key, ?> existCallable = tasks.get(task.getKey());
        if (existCallable != null && existCallable.getStatus().isRunning()) {
            log.warn("Task with key [{}] does exists", task.getKey());
            return false;
        }

        return true;
    }

    @Override
    public boolean cancel(Key key) {
        TaskStatusCallable<Key, ?> callable = tasks.get(key);
        if (callable == null) {
            log.warn("The task with key [" + key + "] is not found");
            return false;
        }

        if (!callable.getStatus().isRunning()) {
            log.info("The status of task [" + key + "] is " + callable.getStatus());
            return false;
        }

        boolean success = false;
        Future<?> future = callable.getFuture();
        if (future != null) {
            success = future.cancel(true);
        } else {
            log.debug("The future is not found by key [{}]", key);
            success = true;
        }

        if (success) {
            callable.setStatus(ExecuteStatus.Cancel);
            finishCallable(callable);
        }
        return success;
    }

    @Override
    public boolean remove(Key key) {
        return tasks.remove(key) != null;
    }

    protected void finishCallable(final TaskStatusCallable<Key, ?> callable) {
        taskRemoveStrategy.removeTask(this, callable.getTask().getKey(), callable.getStatus());
    }

    @Override
    public Collection<? extends TaskStatus<Key, ?>> getTasksStatus() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public TaskStatusCallable<Key, ?> getTaskStatus(Key key) {
        TaskStatusCallable<Key, ?> taskStatus = tasks.get(key);
        if (taskStatus == null) {
            return null;
        }

        return taskStatus;
    }

    public void setMaxThreadSize(int threadSize) {
        threadPoolExecutor.setCorePoolSize(threadSize);
        threadPoolExecutor.setMaximumPoolSize(threadSize);
    }

    @Override
    public void shutdown() {
        threadPoolExecutor.shutdown();
        taskRemoveStrategy.shutdown();
        tasks.clear();
    }

    public void setTaskFailedStrategy(TaskFailedStrategy taskFailedStrategy) {
        this.taskFailedStrategy = taskFailedStrategy;
    }

    public void setException2Message(Exception2Message exception2Message) {
        this.exception2Message = exception2Message;
    }

    public void setSynchronizationExecutor(ISynchronizationExecutor<Key> synchronizationExecutor) {
        this.synchronizationExecutor = synchronizationExecutor;
    }

    public void setTaskRemoveStrategy(TaskRemoveStrategy taskRemoveStrategy) {
        this.taskRemoveStrategy = taskRemoveStrategy;
    }

    public TaskFailedStrategy getTaskFailedStrategy() {
        return taskFailedStrategy;
    }

    public Exception2Message getException2Message() {
        return exception2Message;
    }

    public TaskRemoveStrategy getTaskRemoveStrategy() {
        return taskRemoveStrategy;
    }

    public ISynchronizationExecutor<Key> getSynchronizationExecutor() {
        return synchronizationExecutor;
    }

    private static class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

        public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                          BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        protected <Result> RunnableFuture<Result> newTaskFor(Callable<Result> callable) {
            return new PriorityFutureTask((TaskStatusCallable) callable);
        }

        private static class PriorityFutureTask<Key, Result> extends FutureTask<Result> implements
                Comparable<PriorityFutureTask<Key, Result>> {

            private TaskStatusCallable<Key, Result> callable;

            public PriorityFutureTask(TaskStatusCallable<Key, Result> callable) {
                super(callable);
                this.callable = callable;
            }

            @Override
            public int compareTo(PriorityFutureTask<Key, Result> other) {
                return this.callable.compareTo(other.callable);
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((callable == null) ? 0 : callable.hashCode());
                return result;
            }

            @SuppressWarnings("rawtypes")
            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                PriorityFutureTask other = (PriorityFutureTask) obj;
                if (callable == null) {
                    if (other.callable != null)
                        return false;
                } else if (!callable.equals(other.callable))
                    return false;
                return true;
            }

        }

    }

    /**
     * A wrapper of callable which provide the submiting time,the executing
     * time, the status and the future.
     */
    protected static class TaskStatusCallable<Key, Result> implements Callable<Result>, Comparable<TaskStatusCallable<Key, Result>>,
            TaskStatus<Key, Result> {

        private final Callback<Key, Result> callback;
        private final Task<Key, Result> task;
        private final TaskFailedStrategy taskFailedStrategy;
        private final Exception2Message exception2Message;
        private final ISynchronizationExecutor<Key> synchronizationExecutor;

        private Future<Result> future;
        private ExecuteStatus status = ExecuteStatus.Wait;
        private long timestamp;
        private LocalDateTime sumitTime;
        private LocalDateTime executeTime;
        private LocalDateTime finishTime;
        private String message = "";

        public TaskStatusCallable(Task<Key, Result> task, Callback<Key, Result> callback, TaskFailedStrategy taskFailedStrategy,
                                  Exception2Message exception2Message, ISynchronizationExecutor<Key> synchronizationExecutor) {
            if (task == null) {
                throw new IllegalArgumentException("task is required");
            }
            this.task = task;
            this.callback = callback;
            this.taskFailedStrategy = taskFailedStrategy;
            this.exception2Message = exception2Message;
            this.synchronizationExecutor = synchronizationExecutor;

            this.timestamp = System.nanoTime();
            this.sumitTime = LocalDateTime.now();
        }

        public void setStatus(ExecuteStatus status) {
            this.status = status;
        }

        @Override
        public Result call() throws Exception {
            if (synchronizationExecutor == null) {
                return doCall();
            } else {
                status = ExecuteStatus.Lock;
                return synchronizationExecutor.synchronizedExecute(new Callable<Result>() {

                    @Override
                    public Result call() throws Exception {
                        return doCall();
                    }
                }, task.getKey());
            }
        }

        protected Result doCall() throws Exception {
            status = ExecuteStatus.Executing;
            executeTime = LocalDateTime.now();

            try {
                Exception exception = null;
                Result result = null;
                try {
                    result = this.task.call();
                } catch (Exception e) {
                    exception = e;
                } catch (Error e) {
                    exception = new IllegalStateException(e);
                }

                finishTime = LocalDateTime.now();
                if (taskFailedStrategy.isFailed(exception)) {
                    status = ExecuteStatus.Error;
                    exception = taskFailedStrategy.toException(this.task, exception);
                    log.error("Unexpected exception", exception);
                    message = exception2Message.toMessage(exception);
                    throw exception;
                } else {
                    status = ExecuteStatus.Finish;
                }
                return result;
            } finally {
                if (status == ExecuteStatus.Executing) {
                    status = ExecuteStatus.Error;
                }
                if (callback != null) {
                    callback.invoke(this);
                }
            }
        }

        @Override
        public Task<Key, Result> getTask() {
            return this.task;
        }

        public void setFuture(Future<Result> future) {
            this.future = future;
        }

        @Override
        public Future<Result> getFuture() {
            return future;
        }

        @Override
        public ExecuteStatus getStatus() {
            return status;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public long getCostInMillis() {
            LocalDateTime endTime = this.finishTime != null ? this.finishTime : LocalDateTime.now();
            return sumitTime.until(endTime, ChronoUnit.MILLIS);
        }

        @Override
        public long getExecutingInMillis() {
            if (this.executeTime != null && this.finishTime != null) {
                return executeTime.until(finishTime, ChronoUnit.MILLIS);
            }
            return 0;
        }

        @Override
        public int getPriority() {
            return task.getPriority();
        }

        @Override
        public LocalDateTime getSumitTime() {
            return sumitTime;
        }

        @Override
        public LocalDateTime getExecuteTime() {
            return executeTime;
        }

        @Override
        public LocalDateTime getFinishTime() {
            return finishTime;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public int compareTo(TaskStatusCallable<Key, Result> other) {
            int value = other.getPriority() - this.getPriority();
            if (value != 0) {
                return value;
            }
            return Long.valueOf(this.timestamp).compareTo(Long.valueOf(other.timestamp));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((executeTime == null) ? 0 : executeTime.hashCode());
            result = prime * result + ((status == null) ? 0 : status.hashCode());
            result = prime * result + ((sumitTime == null) ? 0 : sumitTime.hashCode());
            result = prime * result + ((task == null) ? 0 : task.hashCode());
            result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
            return result;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TaskStatusCallable other = (TaskStatusCallable) obj;
            if (executeTime == null) {
                if (other.executeTime != null)
                    return false;
            } else if (!executeTime.equals(other.executeTime))
                return false;
            if (status != other.status)
                return false;
            if (sumitTime == null) {
                if (other.sumitTime != null)
                    return false;
            } else if (!sumitTime.equals(other.sumitTime))
                return false;
            if (task == null) {
                if (other.task != null)
                    return false;
            } else if (!task.equals(other.task))
                return false;
            if (timestamp != other.timestamp)
                return false;
            return true;
        }

    }

    private static class DefaultTaskFailedStrategy implements TaskFailedStrategy {

        @Override
        public boolean isFailed(Throwable throwable) {
            return throwable != null;
        }

        @Override
        public <Key, Result> Exception toException(Task<Key, Result> task, Exception exception) {
            return exception;
        }

    }

    private static class DefaultException2Message implements Exception2Message {

        @Override
        public String toMessage(Exception e) {
            return toErrorMessage(e);
        }

        private static String toErrorMessage(Throwable e) {
            if (e == null) {
                return "";
            }
            if (e.getCause() == null) {
                return e.toString();
            }
            return new StringBuilder(e.toString()).append("|").append(toErrorMessage(e.getCause())).toString();
        }

    }

    public static class FinishTaskDelayRemoveStrategy implements TaskRemoveStrategy {

        protected final Logger log = LoggerFactory.getLogger(getClass());
        public static final int DEFAULT_FINISH_TASK_LIVE_TIME = 60;

        private int successTaskLiveTime = DEFAULT_FINISH_TASK_LIVE_TIME;
        private int errorTaskLiveTime = successTaskLiveTime * 5;

        private final ScheduledExecutorService scheduledService;

        public FinishTaskDelayRemoveStrategy() {
            scheduledService = Executors.newSingleThreadScheduledExecutor();
        }

        public void setSuccessTaskLiveTime(int successTaskLiveTime) {
            this.successTaskLiveTime = successTaskLiveTime;
        }

        public void setErrorTaskLiveTime(int errorTaskLiveTime) {
            this.errorTaskLiveTime = errorTaskLiveTime;
        }

        @Override
        public <Key> void removeTask(final TaskExecuteManager<Key> taskManager, final Key key, final ExecuteStatus taskStatus) {
            int delay = successTaskLiveTime;
            if (ExecuteStatus.Error.equals(taskStatus)) {
                delay = errorTaskLiveTime;
            }

            final TaskStatus<Key, ?> targetTaskStatus = taskManager.getTaskStatus(key);
            if (targetTaskStatus == null) {
                return;
            }

            scheduledService.schedule(new Runnable() {

                @Override
                public void run() {
                    TaskStatus<Key, ?> currentTaskStatus = taskManager.getTaskStatus(key);
                    if (currentTaskStatus == null) {
                        return;
                    }

                    if (!targetTaskStatus.getSumitTime().equals(currentTaskStatus.getSumitTime())) {
                        log.debug("Task [{}] with status [{}] is a new instane and could not be remove by this schedule", key,
                                currentTaskStatus.getStatus());
                        return;
                    }

                    log.debug("Remove task [{}] with status [{}] and submitTime [{}]", new Object[]{key, currentTaskStatus.getStatus(),
                            currentTaskStatus.getSumitTime()});
                    taskManager.remove(key);
                }
            }, delay, TimeUnit.SECONDS);
        }

        @Override
        public void shutdown() {
            scheduledService.shutdown();
        }

    }

    private static interface Callback<Key, Result> {

        void invoke(TaskStatusCallable<Key, Result> task);

    }

}
