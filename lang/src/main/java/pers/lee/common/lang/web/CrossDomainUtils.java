package pers.lee.common.lang.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

public class CrossDomainUtils {
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    public static final String REDIRECT_URL_KEY = "Redirect-URL";
    public static final String REDIRECT_METHOD = "Redirect-Method";
    public static final String REDIRECT_HEADERS = "Redirect-Headers";
    public static final String REDIRECT_CERT = "Redirect-Cert";
    public static final String REDIRECT_AUTH = "Redirect-Auth";
    public static final String REDIRECT_BODY = "Redirect-Body";
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = new StringJoiner(",")
            .add(REDIRECT_AUTH)
            .add(REDIRECT_BODY)
            .add(REDIRECT_CERT)
            .add(REDIRECT_HEADERS)
            .add(REDIRECT_METHOD)
            .add(REDIRECT_URL_KEY)
            .add(CONTENT_TYPE)
            .toString();

    public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = new StringJoiner(",")
            .add("DELETE")
            .add("GET")
            .add("POST")
            .add("PUT")
            .add("OPTIONS")
            .add("TRACE")
            .add("HEAD")
            .toString();

    public static void process(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_HEADERS_VALUE);
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE);
    }
}
