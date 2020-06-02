package pers.lee.common.rpc.client;

import pers.lee.common.lang.client.UrlWrapper;

/**
 * RpcClient
 *
 * @author Drizzt Yang
 */
public interface RpcClient {

    /**
     * @param urlWrapper
     * @param method
     * @param parameters
     * @return
     */
    Object service(UrlWrapper urlWrapper, String method, Object... parameters);

    /**
     * @param urlWrapper
     * @param method
     * @param clazz
     * @param parameters
     * @param <T>
     * @return
     */
    <T> T service(UrlWrapper urlWrapper, String method, Class<T> clazz, Object... parameters);

    /**
     * @param url
     * @param method
     * @param parameters
     * @return
     */
    default Object service(String url, String method, Object... parameters) {
        return service(UrlWrapper.defaultUrlWrapper(url), method, parameters);
    }

    /**
     * @param url
     * @param method
     * @param clazz
     * @param parameters
     * @param <T>
     * @return
     */
    default <T> T service(String url, String method, Class<T> clazz, Object... parameters) {
        return service(UrlWrapper.defaultUrlWrapper(url), method, clazz, parameters);
    }


}
