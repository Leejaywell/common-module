package pers.lee.commom.kafka.admin;

import pers.lee.commom.kafka.ConfigKeys;
import pers.lee.common.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * @author: Jay.Lee
 * @date: 2019/3/18
 */
public class KafkaContainerMessageListenerChecker implements Runnable, InitializingBean {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final int DEFAULT_TASK_DELAY_SECOND = 60;
    private TaskScheduler taskScheduler;
    private KafkaMessageListenerContainerAdmin admin;
    private int checkDelay = DEFAULT_TASK_DELAY_SECOND;
    private boolean enabled = true;

    public KafkaContainerMessageListenerChecker(KafkaMessageListenerContainerAdmin admin) {
        this.admin = admin;
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("CheckKafkaMessageListenerContainer-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    @Override
    public void run() {
        log.info("start check container status");
        admin.listStatus().forEach((topic, isRunning) -> {
                    if (!isRunning) {
                        start(topic);
                    }
                }
        );
    }

    private void start(String topic) {
        if (!enabled) {
            return;
        }
        log.info("restart container[{}]", topic);
        admin.start(topic);
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void setCheckDelay(int checkDelay) {
        this.checkDelay = checkDelay;
    }

    @Config(ConfigKeys.GO_KAFKA_CONTAINER_CHECKER_SCHEDULER_ENABLE)
    public void setEnabled(boolean enabled) {
        this.enabled = Optional.ofNullable(enabled).orElse(true);
    }

    @Override
    public void afterPropertiesSet() {
        log.info("start scan container status task");
        taskScheduler.scheduleWithFixedDelay(this, Instant.now().plus(60, ChronoUnit.SECONDS), Duration.ofSeconds(checkDelay));
    }
}
