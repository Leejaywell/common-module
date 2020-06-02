package pers.lee.common.rpc.server;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

/**
 * @author YangYang
 * @version 0.1, 2008-10-16 12:12:49
 */
public abstract class RpcConsole {
    private String charset = "UTF-8";

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcConsole.class);

    public void invoke(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        OutputStream originOutputStream = outputStream;
        // if the LOGGER is debug enabled, LOGGER the stream
        Logger streamLogger = getStreamLogger(context.getDomain());
        String requestId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        if (streamLogger.isInfoEnabled()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteArrayOutputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            String content = new String(byteArrayOutputStream.toByteArray(), getCharset());
            traceRequestStreamLog(streamLogger, requestId, content);
            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            outputStream = new ByteArrayOutputStream();
        }

        Object request = unmarshal(inputStream);
        Object response = process(request, context);
        marshal(outputStream, response);

        if (streamLogger.isInfoEnabled()) {
            ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) outputStream;
            String content = new String(byteArrayOutputStream.toByteArray(), getCharset());
            traceResponseStreamLog(streamLogger, requestId, content);
            IOUtils.copy(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), originOutputStream);
        }
    }

    /**
     * invoke the service related to the request, and wrap the result to a response
     *
     * @param request the request wrapper
     * @param context the rpc context
     * @return the response wrapper
     */
    protected abstract Object process(Object request, Context context);

    protected abstract Object unmarshal(InputStream inputStream) throws IOException;

    protected abstract void marshal(OutputStream outputStream, Object object) throws IOException;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public abstract boolean accept(Context context);

    protected void traceRequestStreamLog(Logger logger, String requestId, String request) {
        if (request != null) {
            logger.info(requestId + " Receive request <| " + request);
        }
    }

    protected void traceResponseStreamLog(Logger logger, String requestId, String response) {
        if (response != null) {
            logger.info(requestId + " Send response >| " + response);
        }
    }

    protected Logger getStreamLogger(String alias) {
        return LoggerFactory.getLogger("http.StreamLog.rpc." + alias);
    }
}
