package pers.lee.common.rpc.server;

import java.lang.reflect.Method;

/**
 * @author: Jay.Lee
 * @date: 2019/4/19
 */
public interface AnnotationMethodInvokerFactory extends MethodInvokerFactory {

    @Override
    default MethodInvoker getMethodInvoker(String key) throws IllegalConfigurationException {
        return null;
    }

    MethodInvoker getMethodInvoker(Method method);
}
