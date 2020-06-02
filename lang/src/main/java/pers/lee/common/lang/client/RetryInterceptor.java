package pers.lee.common.lang.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.RouteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Passyt on 2018/7/25.
 */
public class RetryInterceptor implements Interceptor {

    private Logger log = LoggerFactory.getLogger(RetryInterceptor.class);

    private int retryTimes = 3;
    private long retryInterval = 100;
    private Set<Integer> retryStatusCodes = new HashSet<>();
    private boolean traceLog = true;

    public RetryInterceptor(int retryTimes, long retryInterval) {
        this.retryTimes = retryTimes;
        this.retryInterval = retryInterval;
        setDefaultRetryStatusCodes();
    }

    protected void setDefaultRetryStatusCodes() {
        retryStatusCodes.add(503);
        retryStatusCodes.add(404);
    }

    public RetryInterceptor addRetryStatusCode(int statusCode) {
        this.retryStatusCodes.add(statusCode);
        return this;
    }

    public RetryInterceptor traceLog(boolean traceLog) {
        this.traceLog = traceLog;
        return this;
    }

    public void setRetryStatusCodes(Set<Integer> retryStatusCodes) {
        this.retryStatusCodes = retryStatusCodes;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        for (int i = 0; i <= retryTimes; i++) {
            try {
                Response response = chain.proceed(request);
                if (i != retryTimes && retryStatusCodes.contains(response.code())) {
                    if (traceLog) {
                        log.warn("Retry (" + (i + 1) + "/" + retryTimes + ") times by url [" + request.url() + "] with status code [" + response.code() + "]");
                    }
                    continue;
                }
                return response;
            } catch (Throwable t) {
                if (i != retryTimes && isRetryable(t)) {
                    if (traceLog) {
                        log.warn("Retry " + (i + 1) + "/" + retryTimes + " times by url [" + request.url() + "]", t);
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryInterval);
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
                throw t;
            }
        }

        throw new IllegalStateException("Unreachable code");
    }

    protected boolean isRetryable(Throwable t) {
        IOException e = null;
        if (t instanceof RouteException) {
            e = RouteException.class.cast(t).getLastConnectException();
        } else if (t instanceof IOException) {
            e = IOException.class.cast(t);
        }

        if (e == null) {
            return false;
        }

        if (e instanceof ProtocolException) {
            return false;
        }

        // If there was an interruption don't recover, but if there was a timeout connecting to a route
        // we should try the next route (if there is one).
        if (e instanceof InterruptedIOException) {
            return e instanceof SocketTimeoutException;
        }

        // Look for known client-side or negotiation errors that are unlikely to be fixed by trying
        // again with a different route.
        if (e instanceof SSLHandshakeException) {
            // If the problem was a CertificateException from the X509TrustManager,
            // do not retry.
            if (e.getCause() instanceof CertificateException) {
                return false;
            }
        }
        if (e instanceof SSLPeerUnverifiedException) {
            // e.g. a certificate pinning error.
            return false;
        }

        // An example of one we might want to retry with a different route is a problem connecting to a
        // proxy and would manifest as a standard IOException. Unless it is one we know we should not
        // retry, we return true and try a new route.
        return true;
    }

}
