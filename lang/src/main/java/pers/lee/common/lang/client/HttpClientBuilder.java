package pers.lee.common.lang.client;

import pers.lee.common.lang.ssl.AlwaysTrustHostnameVerifier;
import pers.lee.common.lang.ssl.AlwaysTrustSSLSocketFactory;
import pers.lee.common.lang.ssl.AlwaysX509TrustManager;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Passyt on 2018/5/17.
 */
public class HttpClientBuilder {

    private final OkHttpClient.Builder builder;

    public HttpClientBuilder() {
        this(defaultBuilder());
    }

    public HttpClientBuilder(OkHttpClient.Builder builder) {
        this.builder = builder;
        if (!this.builder.networkInterceptors().stream().filter(e -> e instanceof HttpAccessLoggingInterceptor).findFirst().isPresent()) {
            this.builder.addNetworkInterceptor(new HttpAccessLoggingInterceptor(true));
        }
        if (!this.builder.interceptors().stream().filter(e -> e instanceof HttpStreamLoggingInterceptor).findFirst().isPresent()) {
            this.builder.addInterceptor(new HttpStreamLoggingInterceptor());
        }
    }

    public HttpClient build() {
        return new DefaultHttpClient(builder.build());
    }

    public HttpClient build(Set<Integer> extraSuccessResponseCodes) {
        return new DefaultHttpClient(builder.build(), extraSuccessResponseCodes);
    }

    public static OkHttpClient.Builder defaultBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(300, 5, TimeUnit.MINUTES))
                .hostnameVerifier(new AlwaysTrustHostnameVerifier())
                .sslSocketFactory(new AlwaysTrustSSLSocketFactory(), new AlwaysX509TrustManager())
                .addInterceptor(new RetryInterceptor(3, 100));
    }

}
