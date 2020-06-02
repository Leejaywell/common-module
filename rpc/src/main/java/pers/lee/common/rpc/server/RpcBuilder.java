package pers.lee.common.rpc.server;

import javax.servlet.ServletContext;
import java.util.Map;

/**
 * RpcBuilder
 *
 * @author Drizzt Yang
 */
public interface RpcBuilder {

    String INIT_PARAMETER_RPC_CONFIG = "rpc.config";

    JsonRpcContext build(Map<String, String> initParameters, ServletContext servletContext);
}
