package pers.lee.common.rpc.server;

import java.util.List;


public interface IRpcInvoker {
    Object convertReturn(Object returnObject, Context context);

    List<Object> convertParameters(List<Object> parameters, Context context);
}
