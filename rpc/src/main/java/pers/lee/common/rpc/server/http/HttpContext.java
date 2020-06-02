package pers.lee.common.rpc.server.http;

import pers.lee.common.rpc.server.Context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YangYang
 * @version 0.1, 2008-3-16 20:24:49
 */
public final class HttpContext implements Context {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private ServletContext servletContext;

    public HttpContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ServletContext servletContext) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.servletContext = servletContext;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getDomain() {
        return httpServletRequest.getServletPath();
    }
}
