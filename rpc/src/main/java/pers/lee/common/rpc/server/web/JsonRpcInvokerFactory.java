package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.MethodInvoker;
import pers.lee.common.rpc.server.RpcInvoker;
import pers.lee.common.rpc.server.RpcInvokerFactory;

import java.util.List;

/**
 * @author YangYang
 * @version 0.1, 2008-10-16 14:06:52
 */
public class JsonRpcInvokerFactory implements RpcInvokerFactory {

    @Override
    public RpcInvoker getRpcInvoker(MethodInvoker methodInvoker, List<MethodInvoker> parameterConvertorInvokers, MethodInvoker returnConvertorInvoker) {
        return new JsonRpcInvoker(methodInvoker, parameterConvertorInvokers, returnConvertorInvoker);
    }
}
