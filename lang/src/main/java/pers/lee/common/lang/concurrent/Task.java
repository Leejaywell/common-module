package pers.lee.common.lang.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A <tt>task</tt> is a callable, which has it's priority({@link #getPriority()}
 * ), and the identity ({@link #getKey()})
 *
 * @param <Key>    the identity of task, which is like the id of the record in RDMS
 * @param <Result> the result of executing task
 * @author Passyt
 */
public interface Task<Key, Result> extends Callable<Result> {

    int DEFAULT_PRIORITY = 5;

    /**
     * @return
     */
    int getPriority();

    /**
     * The identity of the task
     *
     * @return
     */
    Key getKey();

    /**
     * The status of the task
     *
     * @param <Key>
     * @param <Result>
     */
    interface TaskStatus<Key, Result> {

        /**
         * @return
         */
        Task<Key, Result> getTask();

        /**
         * The future of executing task
         *
         * @return
         */
        Future<Result> getFuture();

        /**
         * The status of the task
         *
         * @return
         */
        ExecuteStatus getStatus();

        /**
         * The priority of the task
         *
         * @return
         */
        int getPriority();

        /**
         * The submit timestampe
         *
         * @return
         */
        LocalDateTime getSumitTime();

        /**
         * The executing timestamp
         *
         * @return
         */
        LocalDateTime getExecuteTime();

        /**
         * The finish timestamp
         *
         * @return
         */
        LocalDateTime getFinishTime();

        /**
         * The cost time
         *
         * @return
         */
        long getCostInMillis();

        /**
         * @return
         */
        long getExecutingInMillis();

        /**
         * @return
         */
        String getMessage();
    }

}
