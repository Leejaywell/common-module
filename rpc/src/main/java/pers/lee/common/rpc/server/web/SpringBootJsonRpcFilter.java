package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.RpcBuilder;

/**
 * @author: Jay
 * @date: 2018/4/24
 */
public class SpringBootJsonRpcFilter extends JsonRpcFilter {
    private SpringBootJsonBuilder springBootJsonBuilder;

    public SpringBootJsonRpcFilter(SpringBootJsonBuilder springBootJsonBuilder) {
        this.springBootJsonBuilder = springBootJsonBuilder;
    }

    @Override
    protected RpcBuilder getRPCBuilder() {
        return springBootJsonBuilder;
    }
}
