package pers.lee.common.lang.client;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Passyt on 2018/12/14.
 */
public class HttpAccessLoggingInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAccessLoggingInterceptor.class);

    private boolean logHeaders = false;

    public HttpAccessLoggingInterceptor(boolean logHeaders) {
        this.logHeaders = logHeaders;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        traceRequestHeaders(request);

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        traceAccessLog(chain, request, response, tookMs);
        traceResponseHeaders(response);

        return response;
    }

    protected void traceRequestHeaders(Request request) {
        if (!logHeaders) {
            return;
        }

        LOGGER.debug("HTTP Request Data");
        Headers requestHeaders = request.headers();
        for (int i = 0, count = requestHeaders.size(); i < count; i++) {
            String name = requestHeaders.name(i);
            LOGGER.debug(" >> " + name + ": " + requestHeaders.value(i));
        }
    }

    protected void traceResponseHeaders(Response response) {
        if (!logHeaders) {
            return;
        }

        LOGGER.debug("HTTP Response Data");
        Headers responseHeaders = response.headers();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            LOGGER.debug(" << " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
    }

    protected void traceAccessLog(Chain chain, Request request, Response response, long tookMs) throws IOException {
        String urlAlias = request.header("url.alias");
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        HttpUrl url = request.url();
        String requestLine = url.url().getPath();
        String queryString = url.encodedQuery();
        if (queryString != null && queryString.length() > 0) {
            requestLine = requestLine + "?" + queryString;
        }
        getAccessLogger(urlAlias).debug("{}|{}:{} \"{} {} {}\" {} {} rt={} {}",
                connection == null ? url.host() : connection.socket().getInetAddress().getHostAddress(),
                url.host(),
                url.port(),
                request.method(),
                requestLine,
                protocol.toString(),
                response.code(),
                request.body() == null ? 0 : request.body().contentLength(),
                tookMs,
                response.body().contentLength()
        );
    }

    protected Logger getAccessLogger(String urlAlias) {
        String loggerName = "http.AccessLog";
        if (urlAlias != null) {
            loggerName = loggerName + "." + urlAlias;
        }
        return LoggerFactory.getLogger(loggerName);
    }

}
