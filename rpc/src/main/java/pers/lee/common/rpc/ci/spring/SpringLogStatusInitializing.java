package pers.lee.common.rpc.ci.spring;

import pers.lee.common.config.ApplicationConfiguration;
import pers.lee.common.config.spring.event.ConfigChangeEvent;
import pers.lee.common.rpc.ci.status.StatusCenter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

/**
 * SpringLogStatusInitializing
 *
 * @author Drizzt Yang
 */
public class SpringLogStatusInitializing extends DefaultLogStatusInitializing implements InitializingBean, ApplicationListener<ConfigChangeEvent> {
    private ApplicationConfiguration applicationConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
        initAppLog(applicationConfiguration);
        initHttpAccessLog(applicationConfiguration);
    }

    @Override
    public void onApplicationEvent(ConfigChangeEvent configChangeEvent) {
        updateLogFilePath(configChangeEvent.getKey(), configChangeEvent.getValue());
        updateHttpAccessLogFilePath(configChangeEvent.getKey(), configChangeEvent.getValue());
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public void setStatusCenter(StatusCenter statusCenter) {
        this.statusCenter = statusCenter;
    }
}
