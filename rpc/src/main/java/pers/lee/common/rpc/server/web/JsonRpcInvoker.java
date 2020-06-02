package pers.lee.common.rpc.server.web;

import pers.lee.common.lang.json.Json;
import pers.lee.common.rpc.server.Context;
import pers.lee.common.rpc.server.MethodInvoker;
import pers.lee.common.rpc.server.RpcInvoker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author YangYang
 * @version 0.1, 2008-3-21 16:16:31
 */
public class JsonRpcInvoker extends RpcInvoker {
    public JsonRpcInvoker(MethodInvoker coreMethodInvoker, List<MethodInvoker> parameterConvertorInvokers, MethodInvoker returnConvertorInvoker) {
        super(coreMethodInvoker, parameterConvertorInvokers, returnConvertorInvoker);
    }

    public Object convertReturn(Object returnObject, Context context) {
        Object result = super.convertReturn(returnObject, context);
        return result;
    }

    public List<Object> convertParameters(List<Object> parameters, Context context) {
        List<Object> convertedParameters = super.convertParameters(parameters, context);
        for (int i = 0; i < convertedParameters.size(); i++) {
            Type parameterType = getMethod().getGenericParameterTypes()[i];
            Object value = convertedParameters.get(i);
            if (!getMethod().getParameterTypes()[i].isInstance(value) || parameterType instanceof ParameterizedType) {
                convertedParameters.set(i, Json.getDefault().toJavaObject(value, parameterType));
            }
        }
        return convertedParameters;
    }
}

