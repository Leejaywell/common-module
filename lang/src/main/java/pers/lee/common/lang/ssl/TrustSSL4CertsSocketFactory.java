package pers.lee.common.lang.ssl;

import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Base64;

public final class TrustSSL4CertsSocketFactory extends SSLSocketFactory {
    private static final String SSL = "SSL";
    private SSLSocketFactory sslSocketFactory;

    public TrustSSL4CertsSocketFactory(String type, String cert, String certPassword) {
        try {
            SSLContext sslContext = SSLContext.getInstance(SSL);
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(new ByteArrayInputStream(Base64.getDecoder().decode(cert)), certPassword.toCharArray());
            KeyManagerFactory kmFact = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmFact.init(ks, certPassword.toCharArray());
            KeyManager[] kms = kmFact.getKeyManagers();
            sslContext.init(kms, new TrustManager[]{new AlwaysX509TrustManager()}, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error("TrustSSL4CertsSocketFactory init failed", e);
            throw new RuntimeException("Invalid Cert Error!", e);
        }
    }

    public String[] getDefaultCipherSuites() {
        return sslSocketFactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return sslSocketFactory.getSupportedCipherSuites();
    }

    public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
        return sslSocketFactory.createSocket(socket, s, i, b);
    }

    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(s, i);
    }

    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(s, i, inetAddress, i1);
    }

    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return sslSocketFactory.createSocket(inetAddress, i);
    }

    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return sslSocketFactory.createSocket(inetAddress, i, inetAddress1, i1);
    }

    public Socket createSocket() throws IOException {
        return sslSocketFactory.createSocket();
    }
}
