package pers.lee.common.rpc.server;

/**
 * @author YangYang
 * @version 0.1, 2008-3-17 15:50:17
 */
public interface MethodInvokerFactory {

    MethodInvoker getMethodInvoker(String key) throws IllegalConfigurationException;
    
}
