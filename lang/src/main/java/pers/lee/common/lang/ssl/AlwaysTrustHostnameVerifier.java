package pers.lee.common.lang.ssl;

import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author yangyang
 * @since 2009-5-8
 */
public final class AlwaysTrustHostnameVerifier implements HostnameVerifier {
    public boolean verify(String s, SSLSession sslSession) {
        LoggerFactory.getLogger(this.getClass()).debug("Ignore host name verifying");
        return true;
    }
}
