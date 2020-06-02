package pers.lee.common.lang.utils;

import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;
import pers.lee.common.lang.client.*;
import pers.lee.common.lang.ssl.AlwaysTrustHostnameVerifier;
import pers.lee.common.lang.ssl.AlwaysTrustSSLSocketFactory;
import pers.lee.common.lang.ssl.AlwaysX509TrustManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Passyt on 2018/9/13.
 */
public class AwsHelper {

    public static final String META_URL = "http://169.254.169.254/latest/meta-data";
    public static final String LOCAL_IPV4 = "local-ipv4";
    public static final String PUBLIC_IPV4 = "public-ipv4";
    public static final String INSTANCE_TYPE = "instance-type";
    public static final String INSTANCE_ID = "instance-id";

    public static final Map<String, String> KEYS = new HashMap<>();
    private final static AwsHelper INSTANCE = new AwsHelper();
    private static boolean IS_EC2 = false;

    private final HttpClient client = httpClient(500, 1000, TimeUnit.MILLISECONDS);

    static {
        KEYS.put(LOCAL_IPV4, "internal.address");
        KEYS.put(PUBLIC_IPV4, "external.address");
        KEYS.put(INSTANCE_TYPE, "aws.instance.type");
        KEYS.put(INSTANCE_ID, "aws.instance.id");

        try {
            try (InputStream in = Runtime.getRuntime().exec("cat /sys/hypervisor/uuid").getInputStream()) {
                String string = IOUtils.toString(in, "UTF-8");
                IS_EC2 = string.startsWith("ec2");
            }
        } catch (IOException e) {
        }
    }

    private AwsHelper() {
    }

    public static AwsHelper getInstance() {
        return INSTANCE;
    }

    public boolean isEC2() {
        return IS_EC2;
    }

    public Map<String, String> metaInfo() {
        return metaInfo(KEYS.keySet().toArray(new String[0]));
    }

    public Map<String, String> metaInfo(String... metaKeys) {
        Map<String, String> info = new HashMap<>();
        if (!isEC2()) {
            return info;
        }

        for (String metaKey : metaKeys) {
            setInfo(info, metaKey, Optional.ofNullable(KEYS.get(metaKey)).orElse("aws." + metaKey.replace("-", ".")));
        }
        return info;
    }

    private void setInfo(Map<String, String> info, String metaKey, String infoKey) {
        Optional.ofNullable(read(metaKey)).ifPresent(infoValue -> info.put(infoKey, infoValue));
    }

    protected String read(String metaKey) {
        try {
            return client.get(new UrlWrapper(META_URL + "/" + metaKey, "server.meta"));
        } catch (Throwable e) {
            return null;
        }
    }

    static HttpClient httpClient(long connectTimeout, long readTimeout, TimeUnit unit) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, unit)
                .readTimeout(readTimeout, unit)
                .hostnameVerifier(new AlwaysTrustHostnameVerifier())
                .sslSocketFactory(new AlwaysTrustSSLSocketFactory(), new AlwaysX509TrustManager());

        RetryInterceptor retryInterceptor = new RetryInterceptor(0, 100).traceLog(false);
        String value = System.getProperty("server.meta.retry.times");
        if (value != null && value.trim().length() > 0) {
            int retryTimes = Integer.parseInt(value.trim());
            retryInterceptor = new RetryInterceptor(retryTimes, Integer.parseInt(System.getProperty("server.meta.retry.interval", "100"))).traceLog(false);
        }

        if ("true".equalsIgnoreCase(System.getProperty("aws.meta.trace"))) {
            builder.addInterceptor(new HttpLoggingInterceptor(true));
            retryInterceptor.traceLog(true);
        }
        builder.addInterceptor(retryInterceptor);
        return new DefaultHttpClient(builder.build());
    }
}
