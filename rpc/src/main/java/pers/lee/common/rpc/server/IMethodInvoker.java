package pers.lee.common.rpc.server;

import java.util.List;


public interface IMethodInvoker {
    Object invoke(List<Object> parameters, Context context);
}
