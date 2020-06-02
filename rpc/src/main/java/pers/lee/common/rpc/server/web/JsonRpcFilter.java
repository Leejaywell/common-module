package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.JsonRpcContext;
import pers.lee.common.rpc.server.RpcBuilder;
import pers.lee.common.rpc.server.http.HttpRPCProcessor;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.Map;

public class JsonRpcFilter extends AbstractRpcFilter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        JsonRpcConsole jsonRpcConsole = new JsonRpcConsole();
        Map<String, String> initParameters = getInitParameters(filterConfig);

		JsonRpcContext jsonRpcContext = getRPCBuilder().build(initParameters, filterConfig.getServletContext());
		jsonRpcConsole.setJsonRpcContext(jsonRpcContext);

        httpRpcProcessor = new HttpRPCProcessor(filterConfig.getServletContext());
        httpRpcProcessor.setConsole(jsonRpcConsole);
    }

    protected RpcBuilder getRPCBuilder() {
        return new JsonRpcBuilder();
    }

}
