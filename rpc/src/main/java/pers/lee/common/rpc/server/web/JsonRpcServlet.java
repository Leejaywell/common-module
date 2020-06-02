package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.JsonRpcContext;
import pers.lee.common.rpc.server.RpcBuilder;
import pers.lee.common.rpc.server.http.HttpRPCProcessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.Map;

/**
 * @author YangYang
 * @author Passyt
 * @version 0.1, 2008-3-10 11:28:06<br/>2010-04-16 10:36:10
 */
public class JsonRpcServlet extends AbstractRpcServlet {
	private static final long serialVersionUID = -8890572040829702632L;

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		JsonRpcConsole jsonRpcConsole = new JsonRpcConsole();
        Map<String, String> initParameters = getInitParameters(servletConfig);

        JsonRpcContext jsonRpcContext = getRPCBuilder().build(initParameters, servletConfig.getServletContext());
		jsonRpcConsole.setJsonRpcContext(jsonRpcContext);
        httpRpcProcessor = new HttpRPCProcessor(servletConfig.getServletContext());
        httpRpcProcessor.setConsole(jsonRpcConsole);
	}

    protected RpcBuilder getRPCBuilder() {
        return new JsonRpcBuilder();
    }
}