package pers.lee.common.config.component;

import pers.lee.common.lang.client.HttpClient;
import pers.lee.common.lang.client.HttpClientBuilder;
import pers.lee.common.lang.json.Json;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * RemoteSource
 *
 * @author Drizzt Yang
 */
public class RemoteSource {

    private final String sourceUrl;
    private final String sourceKey;

    private HttpClient client = new HttpClientBuilder().build();

    public RemoteSource(String sourceUrl, String sourceKey) {
        this.sourceUrl = sourceUrl;
        this.sourceKey = sourceKey;
    }

    public long getCurrentTimestamp() {
        BigDecimal currentTimstamp = execute(BigDecimal.class, "getCurrentTimestamp", sourceKey);
        return currentTimstamp == null ? 0L : currentTimstamp.toBigInteger().longValue();
    }

    public Map<String, String> getAll() {
        return execute(HashMap.class, "getAll", sourceKey);
    }

    public boolean available() {
        Boolean isAvailable = execute(Boolean.class, "available", sourceKey);
        return isAvailable != null ? isAvailable.booleanValue() : false;
    }

    public void setProperty(String key, String value) {
        execute(null, "setProperty", sourceKey, key, value);
    }

    public String getProperty(String key) {
        return execute(null, "getProperty", sourceKey, key);
    }

    protected <T> T execute(Class<T> clazz, String method, Object... parameters) {
        Json json = Json.getDefault();
        Map request = new HashMap();
        request.put("method", method);
        if (parameters != null && parameters.length > 0) {
            request.put("params", json.toJsonObject(Arrays.asList(parameters)));
        }
        String text = client.post(sourceUrl, json.toJsonString(request));
        try {
            Map<String, Object> response = json.deserialize(new StringReader(text), HashMap.class);
            if (response.containsKey("error")) {
                Map error = (Map) response.get("error");
                throw new IllegalStateException(error.get("type") + ":" + (String) error.get("message"));
            }

            if (clazz != null) {
                return json.toJavaObject(response.get("result"), clazz);
            }

            return (T) response.get("result");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
