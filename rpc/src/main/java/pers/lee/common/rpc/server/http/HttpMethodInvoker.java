package pers.lee.common.rpc.server.http;

import pers.lee.common.rpc.server.Context;
import pers.lee.common.rpc.server.MethodInvoker;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YangYang
 * @version 0.1, 2008-3-17 15:20:32
 */
public class HttpMethodInvoker extends MethodInvoker {
    // store the context dependence status
    private List<String> contextDependenceFlags = new ArrayList<String>();

    private static final String DEPENDENCE_FLAG_HTTP_REQUEST = "HttpRequest";
    private static final String DEPENDENCE_FLAG_HTTP_RESPONSE = "HttpResponse";
    private static final String DEPENDENCE_FLAG_SERVLET_CONTEXT = "ServletContext";

    public HttpMethodInvoker(Method method, Object bean) {
        super(method, bean);
        processParameterClasses(method.getParameterTypes());
    }

    @Override
    public Object invoke(List<Object> parameters, Context context) {
        HttpContext httpContext = (HttpContext) context;
        if(parameters == null){
            parameters = new ArrayList<>();
        }
        for (int i = 0; i < contextDependenceFlags.size(); i++) {
            String flag = contextDependenceFlags.get(i);
            if (DEPENDENCE_FLAG_HTTP_REQUEST.equals(flag)) {
                parameters.add(i, httpContext.getHttpServletRequest());
            } else if (DEPENDENCE_FLAG_HTTP_RESPONSE.equals(flag)) {
                parameters.add(i, httpContext.getHttpServletResponse());
            } else if (DEPENDENCE_FLAG_SERVLET_CONTEXT.equals(flag)) {
                parameters.add(i, httpContext.getServletContext());
            }
        }
        return super.invoke(parameters, context);
    }

    private void processParameterClasses(Class<?>[] classes) {
        for (Class<?> clazz : classes) {
            if (HttpServletRequest.class.equals(clazz)) {
                contextDependenceFlags.add(DEPENDENCE_FLAG_HTTP_REQUEST);
            } else if (HttpServletResponse.class.equals(clazz)) {
                contextDependenceFlags.add(DEPENDENCE_FLAG_HTTP_RESPONSE);
            } else if (ServletContext.class.equals(clazz)) {
                contextDependenceFlags.add(DEPENDENCE_FLAG_SERVLET_CONTEXT);
            } else {
                contextDependenceFlags.add("");
            }
        }
    }
}
