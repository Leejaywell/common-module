package pers.lee.common.lang.client;

import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by Passyt on 2018/12/14.
 */
public class HttpStreamLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String requestId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        traceRequestStreamLog(requestId, request);

        Response response = chain.proceed(request);

        traceResponseStreamLog(requestId, request, response);

        return response;
    }

    protected void traceRequestStreamLog(String requestId, Request request) throws IOException {
        String urlAlias = request.header("url.alias");
        String requestStream = toString(request.body());
        if (requestStream != null) {
            getStreamLogger(urlAlias).info("{} request >| {}", requestId, requestStream);
        }
    }

    protected void traceResponseStreamLog(String requestId, Request request, Response response) throws IOException {
        String urlAlias = request.header("url.alias");
        String responseStream = toString(response.body());
        if (responseStream != null) {
            getStreamLogger(urlAlias).info("{} response <| {}", requestId, responseStream);
        }
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
        if (body == null) {
            return null;
        }

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

    protected Logger getStreamLogger(String urlAlias) {
        String loggerName = "http.StreamLog";
        if (urlAlias != null) {
            loggerName = loggerName + "." + urlAlias;
        }
        return LoggerFactory.getLogger(loggerName);
    }

}
