package pers.lee.common.lang.client;

import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Passyt on 2018/5/17.
 */
public class HttpInvocationException extends RuntimeException {

    public HttpInvocationException() {
    }

    public HttpInvocationException(String url, String message) {
        super(message);
    }

    public HttpInvocationException(String url, int statusCode, String responseText) {
        super("[" + url + "]" + " Http unexpected response with code [" + statusCode + "], " + "response [" + responseText + "]");
    }

    public HttpInvocationException(String url, SocketException socketException) {
        super("[" + url + "]" + " SocketException: " + socketException.getMessage(), socketException);
    }

    public HttpInvocationException(String url, SocketTimeoutException socketTimeoutException) {
        super("[" + url + "]" + " SocketTimeoutException: " + socketTimeoutException.getMessage(), socketTimeoutException);
    }

    public HttpInvocationException(String url, Throwable cause) {
        super("[" + url + "]" + " " + cause.getClass().getName() + ": " + cause.getMessage(), cause);
    }

}
