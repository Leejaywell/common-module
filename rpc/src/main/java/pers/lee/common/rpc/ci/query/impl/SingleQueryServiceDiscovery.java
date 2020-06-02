package pers.lee.common.rpc.ci.query.impl;

import pers.lee.common.rpc.ci.query.SingleQueryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author yangyang
 * @since 2009-10-23
 */
public class SingleQueryServiceDiscovery implements ApplicationContextAware, InitializingBean {
	
	private ApplicationContext applicationContext;
	private SingleQueryServiceController singleQueryServiceController;

	public void setSingleQueryServiceController(SingleQueryServiceController singleQueryServiceController) {
		this.singleQueryServiceController = singleQueryServiceController;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void afterPropertiesSet() throws Exception {
		Map<?, ?> beans = this.applicationContext.getBeansOfType(SingleQueryService.class);
		for (Object singleQueryService : beans.values()) {
			singleQueryServiceController.addSingleQueryService((SingleQueryService) singleQueryService);
		}
	}
}
