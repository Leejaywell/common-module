package pers.lee.common.rpc.server;

import pers.lee.common.lang.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Method Invoker
 * @author YangYang
 * @version 0.1, 2008-10-16 13:16:23
 */
public class MethodInvoker implements IMethodInvoker {
    private Method method;

    private Object bean;

    public MethodInvoker(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }

    @Override
    public Object invoke(List<Object> parameters, Context context) {
        try {
            return ReflectUtils.invokeMethod(getMethod(), getBean(), parameters.toArray());
        } catch (RuntimeException e) {
            Throwable throwable = e;
            if(e.getCause() != null) {
                throwable = e.getCause();
            }
            if(throwable instanceof RpcException) {
                throw (RpcException) throwable;
            } else {
                throw new RpcException(throwable.getMessage(), throwable);
            }
        }
    }
}
