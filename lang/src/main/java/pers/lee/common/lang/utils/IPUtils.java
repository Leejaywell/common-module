package pers.lee.common.lang.utils;

import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.client.HttpClient;
import pers.lee.common.lang.client.UrlWrapper;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Passyt on 2018/9/13.
 */
public class IPUtils {

    public static String EXTERNAL_URL = "https://ipinfo.io/json";
    private static HttpClient CLIENT = AwsHelper.httpClient(5, 10, TimeUnit.SECONDS);
    private static boolean READ_IP_INFO = true;

    public static Map<String, String> ipInfo() {
        Map<String, String> values = new HashMap<>();
        try {
            if (READ_IP_INFO) {
                values.putAll(readObject(EXTERNAL_URL));
            }
        } catch (Throwable e) {
            READ_IP_INFO = false;
        }
        values.putAll(AwsHelper.getInstance().metaInfo(AwsHelper.LOCAL_IPV4, AwsHelper.PUBLIC_IPV4));

        if (!values.containsKey("internal.address")) {
            values.put("internal.address", getInternalAddress());
        }
        return values;
    }

    private static Map<String, String> readObject(String url) throws IOException {
        Map<String, String> values = new HashMap<>();
        String response = CLIENT.get(new UrlWrapper(url, "server.meta"));
        Map<String, Object> map = (Map<String, Object>) Json.getDefault().deserialize(new StringReader(response));
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if ("ip".equalsIgnoreCase(entry.getKey())) {
                key = "address";
            }

            values.put("external." + key, String.valueOf(entry.getValue()));
        }
        return values;
    }

    private static String getInternalAddress() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    if (address.isSiteLocalAddress()) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(address.getHostAddress());
                    }
                }
            }
            return sb.toString();
        } catch (Throwable e) {
            return null;
        }
    }

    public static void main(String... a) {
        System.out.println(IPUtils.ipInfo());
    }
}
