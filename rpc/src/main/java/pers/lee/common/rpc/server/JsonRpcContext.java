package pers.lee.common.rpc.server;

/**
 * @author: Jay
 * @date: 2018/4/25
 */
public interface JsonRpcContext extends RpcConfigHolder {

    boolean accept(String domain);

    RpcInvoker getRpcInvoker(String path, String method);

}
