package pers.lee.common.rpc.server.http;

import pers.lee.common.rpc.server.IllegalConfigurationException;
import pers.lee.common.rpc.server.MethodInvoker;
import pers.lee.common.rpc.server.MethodInvokerFactory;

/**
 * @author YangYang
 * @version 0.1, 2008-10-16 13:34:03
 */
public class HttpMethodInvokerFactory implements MethodInvokerFactory {

    private MethodInvokerFactory methodInvokerFactory;

    public HttpMethodInvokerFactory(MethodInvokerFactory methodInvokerFactory) {
        this.methodInvokerFactory = methodInvokerFactory;
    }

    @Override
    public MethodInvoker getMethodInvoker(String key) throws IllegalConfigurationException {
        MethodInvoker methodInvoker = methodInvokerFactory.getMethodInvoker(key);
        return new HttpMethodInvoker(methodInvoker.getMethod(), methodInvoker.getBean());
    }
}
