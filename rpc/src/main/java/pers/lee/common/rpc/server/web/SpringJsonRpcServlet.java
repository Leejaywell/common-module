package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.RpcBuilder;

/**
 * @author Jay
 */
public class SpringJsonRpcServlet extends JsonRpcServlet {

    @Override
    protected RpcBuilder getRPCBuilder() {
        return new SpringJsonRpcBuilder();
    }

}
