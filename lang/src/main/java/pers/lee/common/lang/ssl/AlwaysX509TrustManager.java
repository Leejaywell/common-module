package pers.lee.common.lang.ssl;

import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

/**
 * AlwaysX509TrustManager is the default trust manager. It always returns "true"
 * at both server & client certification check.
 *
 * It was invoked by AlwaysTrustSSLSocketFactory
 * @author yangyang
 * @since 2009-5-8
 * @see AlwaysTrustSSLSocketFactory
 */
public final class AlwaysX509TrustManager implements X509TrustManager {
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
        LoggerFactory.getLogger(this.getClass()).debug("Ignore client certificates checking");
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
        LoggerFactory.getLogger(this.getClass()).debug("Ignore server certificates checking");
    }
}
