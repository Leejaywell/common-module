package pers.lee.common.rpc;

import pers.lee.common.rpc.server.RpcBuilder;
import pers.lee.common.rpc.server.web.JsonRpcBuilder;
import pers.lee.common.rpc.server.web.JsonRpcFilter;
import pers.lee.common.rpc.server.web.SpringJsonRpcBuilder;
import pers.lee.common.rpc.utils.DiscoveryUtils;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author YangYang
 * @version 0.1, 2008-10-16 12:05:52
 */
public class CommonInterfaceFilter extends JsonRpcFilter {

    private boolean mxRPCEnabled = true;

    @Override
    protected RpcBuilder getRPCBuilder() {
        try {
            if (!DiscoveryUtils.discovery("org.springframework.web.context.support.WebApplicationContextUtils")) {
                return new JsonRpcBuilder() {
                    @Override
                    protected Properties buildProperties(Map<String, String> initParameters, ServletContext servletContext) {
                        return loadRPCProperties("common.rpc.properties");
                    }
                };
            }
            return new SpringJsonRpcBuilder() {
                @Override
                protected Properties buildProperties(Map<String, String> initParameters, ServletContext servletContext) {
                    Properties properties = loadRPCProperties("common.spring.rpc.properties");

                    if (mxRPCEnabled) {
                        if (DiscoveryUtils.discovery("org.hibernate.Session")) {
                            properties.putAll(loadRPCProperties("common.spring.hibernate.properties"));
                        } else if (DiscoveryUtils.discovery("com.mongodb.DB")) {
                            properties.putAll(loadRPCProperties("common.spring.mongo.properties"));
                        }
                    }
                    return properties;
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Illegal rpc config properties", e);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String mxRPCEnabled = filterConfig.getInitParameter("mx.enabled");
        if (mxRPCEnabled == null) {
            this.mxRPCEnabled = true;
        } else if (!"true".equalsIgnoreCase(mxRPCEnabled)) {
            this.mxRPCEnabled = false;
        }
        super.init(filterConfig);
    }

    private Properties loadRPCProperties(String classpath) {
        InputStream stream = this.getClass().getResourceAsStream(classpath);
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (Exception e) {
            throw new RuntimeException("load default rpc failed", e);
        }
        return properties;
    }

}
