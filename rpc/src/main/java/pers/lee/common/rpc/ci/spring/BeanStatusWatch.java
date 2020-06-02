package pers.lee.common.rpc.ci.spring;

import pers.lee.common.rpc.ci.status.BeanStatus;
import pers.lee.common.rpc.ci.status.StatusAware;
import pers.lee.common.rpc.ci.status.StatusCenter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

/**
 * BeanStatusWatch
 *
 * @author Drizzt Yang
 */
public class BeanStatusWatch implements BeanPostProcessor {
    private StatusCenter statusCenter;
    private BeanStatusWatchConfiguration statusWatchConfiguration;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof StatusAware) {
            statusCenter.register((StatusAware) bean);
            return bean;
        }

        if (AopUtils.isAopProxy(bean)) {
            return bean;
        }
        String statusKey;
        BeanStatus beanStatusService;
        if (statusWatchConfiguration != null) {
            statusKey = statusWatchConfiguration.getBeanStatusKey(beanName);
            statusKey = statusKey == null ? beanName : statusKey;

            Map<String, String> aliasMap = statusWatchConfiguration.getPropertyStatusKeys(beanName);
            beanStatusService = new BeanStatus(bean, statusKey, aliasMap);
        } else {
            beanStatusService = new BeanStatus(bean, beanName);
        }

        if (beanStatusService.getStatusKeys().size() > 0) {
            statusCenter.register(beanStatusService);
        }
        return bean;
    }

    public void setStatusCenter(StatusCenter statusCenter) {
        this.statusCenter = statusCenter;
    }

    public void setStatusWatchConfiguration(BeanStatusWatchConfiguration statusWatchConfiguration) {
        this.statusWatchConfiguration = statusWatchConfiguration;
    }
}
