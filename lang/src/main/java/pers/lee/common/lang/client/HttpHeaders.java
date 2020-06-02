package pers.lee.common.lang.client;

import java.util.Base64;
import java.util.HashMap;

/**
 * Created by Passyt on 2018/5/18.
 */
public class HttpHeaders extends HashMap<String, String> {

    public static String CONTENT_TYPE = "Content-Type";
    public static String CONTENT_TYPE_JSON = "application/json";
    public static String CONTENT_TYPE_XML = "text/xml";

    public static String AUTHORIZATION = "Authorization";

    public static HttpHeaders defaultHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put("User-Agent", "D Client/2.0");
        return httpHeaders;
    }

    public HttpHeaders addHeader(String name, String value) {
        put(name, value);
        return this;
    }

    public HttpHeaders addHeader4Basic(String userName, String password) {
        return addHeader(AUTHORIZATION, getBasicAuthorization(userName, password));
    }

    public static String getBasicAuthorization(String username, String password) {
        String authorizationValue = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(authorizationValue.getBytes());
    }

}
