package pers.lee.common.rpc.server.web;

import pers.lee.common.rpc.server.JsonRpcContext;
import pers.lee.common.rpc.server.JsonRpcInvokerContext;
import pers.lee.common.rpc.server.RpcBuilder;
import pers.lee.common.rpc.server.SpringBeanMethodInvokerFactory;
import pers.lee.common.rpc.server.http.HttpMethodInvokerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-10-17 17:58:54
 */
public class SpringJsonRpcBuilder extends JsonRpcBuilder implements RpcBuilder {

    @Override
    public JsonRpcContext build(Map<String, String> initParameters, ServletContext servletContext) {
        JsonRpcInvokerContext jsonrpcInvokerContext = new JsonRpcInvokerContext();
        SpringBeanMethodInvokerFactory springBeanMethodInvokerFactory = new SpringBeanMethodInvokerFactory();
        springBeanMethodInvokerFactory.setWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContext));
        jsonrpcInvokerContext.setMethodInvokerFactory(new HttpMethodInvokerFactory(springBeanMethodInvokerFactory));
        jsonrpcInvokerContext.setRPCInvokerFactory(getRPCInvokerFactory());
        jsonrpcInvokerContext.init(buildProperties(initParameters, servletContext));
        return jsonrpcInvokerContext;
    }
}
