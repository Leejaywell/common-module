package pers.lee.common.lang.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author parry
 */
public class CrossDomainFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		CrossDomainUtils.process((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
}
