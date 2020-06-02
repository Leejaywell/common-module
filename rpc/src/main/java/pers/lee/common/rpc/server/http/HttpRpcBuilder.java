package pers.lee.common.rpc.server.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.lee.common.rpc.server.*;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * HttpRpcBuilder
 *
 * @author Drizzt Yang
 */
public abstract class HttpRpcBuilder implements RpcBuilder {
    private static final String CLASSPATH_PREFIX = "classpath:";
    private Logger logger = LoggerFactory.getLogger(HttpRpcBuilder.class);

    @Override
    public JsonRpcContext build(Map<String, String> initParameters, ServletContext servletContext) {
        JsonRpcInvokerContext jsonrpcInvokerContext = new JsonRpcInvokerContext();
        jsonrpcInvokerContext.setMethodInvokerFactory(new HttpMethodInvokerFactory(new FactoryBeanInvokerFactory()));
        jsonrpcInvokerContext.setRPCInvokerFactory(getRPCInvokerFactory());
        jsonrpcInvokerContext.init(buildProperties(initParameters, servletContext));
        return jsonrpcInvokerContext;
    }

    protected abstract RpcInvokerFactory getRPCInvokerFactory();

    protected Properties buildProperties(Map<String, String> initParameters, ServletContext servletContext) {
        String configFile = initParameters.get(INIT_PARAMETER_RPC_CONFIG);

        if (configFile == null) {
            configFile = "rpc.properties";
        }
        InputStream stream;
        if (configFile.startsWith(CLASSPATH_PREFIX)) {
            stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(configFile.substring(CLASSPATH_PREFIX.length()));
        } else {
            stream = servletContext.getResourceAsStream(configFile);
        }
        if (stream == null) {
            logger.warn("No init parameter [" + INIT_PARAMETER_RPC_CONFIG + "]");
            return new Properties();
        }
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            String errorMessage = "Init JsonRpcServlet Processor fails";
            throw new RuntimeException(errorMessage, e);
        }
        return properties;
    }
}
