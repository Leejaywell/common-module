package pers.lee.common.lang.client;

/**
 * Created by Passyt on 2018/5/17.
 */
public interface HttpClient {

    /**
     * @param urlWrapper
     * @param method
     * @param body
     * @param headers
     * @return
     */
    String execute(UrlWrapper urlWrapper, String method, String body, HttpHeaders headers);

    /**
     * @param url
     * @return
     */
    default String get(String url) {
        return get(url, null);
    }

    /**
     * @param url
     * @return
     */
    default String get(String url, HttpHeaders headers) {
        return get(UrlWrapper.defaultUrlWrapper(url), headers);
    }

    /**
     * @param urlWrapper
     * @return
     */
    default String get(UrlWrapper urlWrapper) {
        return get(urlWrapper, null);
    }

    /**
     * @param urlWrapper
     * @return
     */
    default String get(UrlWrapper urlWrapper, HttpHeaders headers) {
        return execute(urlWrapper, "GET", null, httpHeaders(headers));
    }

    /**
     * @param url
     * @param body
     * @return
     */
    default String post(String url, String body) {
        return post(url, body, null);
    }

    /**
     * @param url
     * @param body
     * @return
     */
    default String post(String url, String body, HttpHeaders headers) {
        return post(UrlWrapper.defaultUrlWrapper(url), body, headers);
    }

    /**
     * @param urlWrapper
     * @param body
     * @return
     */
    default String post(UrlWrapper urlWrapper, String body) {
        return post(urlWrapper, body, null);
    }

    /**
     * @param urlWrapper
     * @param body
     * @return
     */
    default String post(UrlWrapper urlWrapper, String body, HttpHeaders headers) {
        return execute(urlWrapper, "POST", body, httpHeaders(headers));
    }

    default HttpHeaders httpHeaders(HttpHeaders headers) {
        HttpHeaders httpHeaders = HttpHeaders.defaultHttpHeaders();
        if (headers != null) {
            httpHeaders.putAll(headers);
        }
        return httpHeaders;
    }

}
