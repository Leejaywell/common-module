package pers.lee.common.rpc.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author YangYang
 * @version 0.1, 2008-3-17 15:52:25
 */
public class RpcInvoker implements IMethodInvoker, IRpcInvoker {

    private MethodInvoker coreMethodInvoker;
    private List<MethodInvoker> parameterConvertorInvokers;
    private MethodInvoker returnConvertorInvoker;

    public RpcInvoker(MethodInvoker coreMethodInvoker, List<MethodInvoker> parameterConvertorInvokers, MethodInvoker returnConvertorInvoker) {
        this.coreMethodInvoker = coreMethodInvoker;
        this.parameterConvertorInvokers = parameterConvertorInvokers;
        this.returnConvertorInvoker = returnConvertorInvoker;
    }

    @Override
    public Object invoke(List<Object> parameters, Context context) {
        Object returnObject = coreMethodInvoker.invoke(convertParameters(parameters, context), context);
        returnObject = convertReturn(returnObject, context);
        return returnObject;
    }

    @Override
    public Object convertReturn(Object returnObject, Context context) {
        if (getReturnConvertorInvoker() != null) {
            returnObject = getReturnConvertorInvoker().invoke(Arrays.asList(returnObject), context);
        }
        return returnObject;
    }

    public Method getMethod() {
        return coreMethodInvoker.getMethod();
    }

    @Override
    public List<Object> convertParameters(List<Object> parameters, Context context) {
        List<Object> convertedParameters = new ArrayList<Object>();
        for (int i = 0; i < getParameterConvertorInvokers().size(); i++) {
            MethodInvoker methodInvoker = getParameterConvertorInvokers().get(i);
            Object parameter;
            if(i < parameters.size()) {
                parameter = parameters.get(i);
            } else {
                break;
            }
            if (methodInvoker != null) {
                parameter = methodInvoker.invoke(Arrays.asList(parameter), context);
            }
            convertedParameters.add(i, parameter);
        }
        return convertedParameters;
    }

    public List<MethodInvoker> getParameterConvertorInvokers() {
        return parameterConvertorInvokers;
    }

    public MethodInvoker getReturnConvertorInvoker() {
        return returnConvertorInvoker;
    }
}
