package pers.lee.common.rpc.server.web;

import pers.lee.common.lang.json.Json;
import pers.lee.common.rpc.server.*;
import pers.lee.common.rpc.server.http.HttpContext;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YangYang
 * @version 0.1, 2008-10-16 12:22:15
 */
public class JsonRpcConsole extends RpcConsole {

    private static final String PARAMETERS = "params";
    private static final String METHOD = "method";
    private static final String RESULT = "result";
    private static final String ERROR = "error";
    private static final String ERROR_TYPE = "type";
    private static final String ERROR_MESSAGE = "message";
    private JsonRpcContext jsonRpcContext;

    @SuppressWarnings("unchecked")
    protected Object process(Object request, Context context) {
        if (!(context instanceof HttpContext)) {
            throw new IllegalArgumentException("Only HttpContext are supported");
        }

        Map<String, Object> response = new HashMap<String, Object>();

        String method = null;
        List parameters = null;
        if (request instanceof Map) {
            Map requestMap = (Map) request;
            if (requestMap.get(METHOD) instanceof String) {
                method = (String) requestMap.get(METHOD);
            }
            if (requestMap.get(PARAMETERS) != null && (requestMap.get(PARAMETERS) instanceof List)) {
                parameters = (List) requestMap.get(PARAMETERS);
            }
        }
        if (method == null) {
            setError(response, "MissingMethod", "RPC method name is missing");
            return response;
        }

        if (parameters == null) {
            parameters = new ArrayList();
        }
        IMethodInvoker methodInvoker = jsonRpcContext.getRpcInvoker(context.getDomain(), method);
        if (methodInvoker == null) {
            setError(response, "UnsupportRPC", "Unsupport RPC [" + method + "] of [" + context.getDomain() + "]");
            return response;
        }

        try {
            Object returnObject = methodInvoker.invoke(parameters, context);
            if (returnObject != null) {
                response.put(RESULT, returnObject);
            }
        } catch (RpcException e) {
            LOGGER.error("Invoke JSONRPC failed [" + e.getErrorCode() + "]", e.getCause());
            setError(response, e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Invoke JSONRPC failed", e);
            setError(response, "SystemError", e.getMessage());
        }
        return response;
    }

    @Override
    public boolean accept(Context context) {
        return jsonRpcContext.accept(context.getDomain());
    }

    private void setError(Map<String, Object> response, String type, String message) {
        Map<String, String> errorObject = new HashMap<String, String>();
        response.put(ERROR, errorObject);
        errorObject.put(ERROR_TYPE, type);
        errorObject.put(ERROR_MESSAGE, message);
    }

    protected Object unmarshal(InputStream inputStream) throws IOException {
        return Json.getDefault().deserialize(new InputStreamReader(inputStream, getCharset()));
    }

    protected void marshal(OutputStream outputStream, Object response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, getCharset());
        Json.getDefault().serialize(writer, response);
        writer.flush();
    }

    public void setJsonRpcContext(JsonRpcContext jsonRpcContext) {
        this.jsonRpcContext = jsonRpcContext;
    }
}
