package pers.lee.common.rpc.server;

import java.util.List;

/**
 * BaseRpcInvokerFactory
 *
 * @author Drizzt Yang
 */
public class BaseRpcInvokerFactory implements RpcInvokerFactory {
    @Override
    public RpcInvoker getRpcInvoker(MethodInvoker methodInvoker, List<MethodInvoker> parameterConvertorInvokers, MethodInvoker returnConvertorInvoker) {
        return new RpcInvoker(methodInvoker, parameterConvertorInvokers, returnConvertorInvoker);
    }
}
