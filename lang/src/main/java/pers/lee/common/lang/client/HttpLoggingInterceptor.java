package pers.lee.common.lang.client;

import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Passyt on 2018/1/15.
 *
 * @see HttpAccessLoggingInterceptor
 * @see HttpStreamLoggingInterceptor
 */
@Deprecated
public class HttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpLoggingInterceptor.class);

    private boolean logHeaders = false;

    public HttpLoggingInterceptor(boolean logHeaders) {
        this.logHeaders = logHeaders;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        traceRequestHeaders(request);

        String requestId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        traceRequestStreamLog(requestId, request);

        long startNs = System.nanoTime();
        Response response = chain.proceed(request);
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        traceAccessLog(chain, request, response, tookMs);
        traceResponseStreamLog(requestId, request, response);
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
                connection == null ? url.host() : connection.socket().getInetAddress().getHostAddress(),
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

    protected void traceRequestStreamLog(String requestId, Request request) throws IOException {
        String urlAlias = request.header("url.alias");
        String requestStream = toString(request.body());
        if (requestStream != null) {
            getStreamLogger(urlAlias).info(requestId + " request >| " + requestStream);
        }
    }

    protected void traceResponseStreamLog(String requestId, Request request, Response response) throws IOException {
        String urlAlias = request.header("url.alias");
        String responseStream = toString(response.body());
        getStreamLogger(urlAlias).info(requestId + " response <| " + responseStream);
    }

    protected String toString(RequestBody body) throws IOException {
        if (body == null) {
            return null;
        }

        Charset charset = UTF8;
        MediaType contentType = body.contentType();
        if (contentType != null) {
            contentType.charset(charset);
        }

        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readString(charset);
    }

    protected String toString(ResponseBody body) throws IOException {
        Charset charset = UTF8;
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();
        return buffer.clone().readString(charset);
    }

    protected Logger getAccessLogger(String urlAlias) {
        String loggerName = "http.AccessLog";
        if (urlAlias != null) {
            loggerName = loggerName + "." + urlAlias;
        }
        return LoggerFactory.getLogger(loggerName);
    }

    protected Logger getStreamLogger(String urlAlias) {
        String loggerName = "http.StreamLog";
        if (urlAlias != null) {
            loggerName = loggerName + "." + urlAlias;
        }
        return LoggerFactory.getLogger(loggerName);
    }

}
