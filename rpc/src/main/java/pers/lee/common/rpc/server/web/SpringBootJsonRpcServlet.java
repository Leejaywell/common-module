package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.RpcBuilder;

/**
 * @author YangYang
 * @version 0.1, 2008-3-20 17:41:03
 */
public class SpringBootJsonRpcServlet extends JsonRpcServlet {
    private SpringBootJsonBuilder springBootJsonBuilder;

    public SpringBootJsonRpcServlet(SpringBootJsonBuilder springBootJsonBuilder) {
        this.springBootJsonBuilder = springBootJsonBuilder;
    }

    @Override
    protected RpcBuilder getRPCBuilder() {
        return springBootJsonBuilder;
    }

}
