package pers.lee.common.rpc.server;

import pers.lee.common.rpc.server.http.HttpMethodInvoker;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author: Jay.Lee
 * @date: 2019/4/19
 */
public class SpringBeanAnnotationMethodInvokerFactory implements AnnotationMethodInvokerFactory {

    private ApplicationContext applicationContext;

    public SpringBeanAnnotationMethodInvokerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        Object bean = applicationContext.getBean(method.getDeclaringClass());
        return createCoreMethodInvoker(method, bean);
    }

    private MethodInvoker createCoreMethodInvoker(Method method, Object serviceBean) {
        return new HttpMethodInvoker(method, serviceBean);
    }
}
