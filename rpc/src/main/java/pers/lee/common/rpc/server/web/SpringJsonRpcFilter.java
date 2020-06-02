package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.RpcBuilder;

/**
 * @author YangYang
 * @version 0.1, 2008-10-17 17:33:09
 */
public class SpringJsonRpcFilter extends JsonRpcFilter {

    @Override
    protected RpcBuilder getRPCBuilder() {
        return new SpringJsonRpcBuilder();
    }

}
