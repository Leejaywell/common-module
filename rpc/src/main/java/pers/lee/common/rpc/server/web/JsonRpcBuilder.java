package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.RpcInvokerFactory;
import pers.lee.common.rpc.server.http.HttpRpcBuilder;

/**
 * JsonRpcBuilder
 *
 * @author Drizzt Yang
 */
public class JsonRpcBuilder extends HttpRpcBuilder {
    protected RpcInvokerFactory getRPCInvokerFactory() {
        return new JsonRpcInvokerFactory();
    }
}
