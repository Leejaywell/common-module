package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.http.HttpRPCProcessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2007-12-6 13:28:58<br/> 0.2, 2010-04-16 10:30:10
 */
public abstract class AbstractRpcServlet extends HttpServlet {

	private static final long serialVersionUID = -3885719121438185611L;
	
	protected HttpRPCProcessor httpRpcProcessor;

	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		httpRpcProcessor.doGet(httpServletRequest, httpServletResponse);
	}

	protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		httpRpcProcessor.doPost(httpServletRequest, httpServletResponse);
	}

    protected Map<String, String> getInitParameters(ServletConfig servletConfig) {
        Map<String, String> initParameters = new HashMap<String, String>();
        Enumeration<String> initParameterNames = servletConfig.getInitParameterNames();
        while(initParameterNames.hasMoreElements())  {
            String name = initParameterNames.nextElement();
            initParameters.put(name, servletConfig.getInitParameter(name));
        }
        return initParameters;
    }
}
