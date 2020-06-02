package pers.lee.common.rpc.server;

import java.util.List;

/**
 * @author YangYang
 * @version 0.1, 2008-10-16 14:02:20
 */
public interface RpcInvokerFactory {

    /**
     * @param methodInvoker
     * @param parameterConvertorInvokers
     * @param returnConvertorInvoker
     * @return
     */
    RpcInvoker getRpcInvoker(MethodInvoker methodInvoker, List<MethodInvoker> parameterConvertorInvokers, MethodInvoker returnConvertorInvoker);
}
