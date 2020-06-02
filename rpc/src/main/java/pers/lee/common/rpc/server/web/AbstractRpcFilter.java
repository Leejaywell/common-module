package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.http.HttpRPCProcessor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangYang
 * @author Passyt
 * @version 0.1, 2008-10-16 12:40:15<br/> 0.2, 2010-04-16 10:30:12
 */
public abstract class AbstractRpcFilter implements Filter {

    protected HttpRPCProcessor httpRpcProcessor;

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException,
            ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

            if (httpRpcProcessor.accept(httpServletRequest, httpServletResponse)) {
                if ("get".equalsIgnoreCase(httpServletRequest.getMethod())) {
                    httpRpcProcessor.doGet(httpServletRequest, httpServletResponse);
                } else if ("post".equalsIgnoreCase(httpServletRequest.getMethod())) {
                    httpRpcProcessor.doPost(httpServletRequest, httpServletResponse);
                }
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
	}

    @Override
    public void destroy() {
        httpRpcProcessor = null;
    }

    protected Map<String, String> getInitParameters(FilterConfig filterConfig) {
        Map<String, String> initParameters = new HashMap<String, String>();
        Enumeration<String> initParameterNames = filterConfig.getInitParameterNames();
        while(initParameterNames.hasMoreElements())  {
            String name = initParameterNames.nextElement();
            initParameters.put(name, filterConfig.getInitParameter(name));
        }
        return initParameters;
    }
}
