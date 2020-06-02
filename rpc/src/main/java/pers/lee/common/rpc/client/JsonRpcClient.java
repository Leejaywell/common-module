package pers.lee.common.rpc.client;

import pers.lee.common.lang.client.HttpClient;
import pers.lee.common.lang.client.HttpClientBuilder;
import pers.lee.common.lang.client.HttpHeaders;
import pers.lee.common.lang.client.UrlWrapper;
import pers.lee.common.lang.json.Json;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Jay
 * @date: 2018/5/11
 */
public class JsonRpcClient implements RpcClient {

    private final HttpClient client;
    private final Json json;

    public JsonRpcClient() {
        this(new HttpClientBuilder().build(), Json.getDefault());
    }

    public JsonRpcClient(HttpClient client) {
        this(client, Json.getDefault());
    }

    public JsonRpcClient(Json json) {
        this(new HttpClientBuilder().build(), json);
    }

    public JsonRpcClient(HttpClient client, Json json) {
        this.client = client;
        this.json = json;
    }

    @Override
    public Object service(UrlWrapper urlWrapper, String method, Object... parameters) {
        String requestBody = toRequest(method, parameters);
        String responseBody = client.post(urlWrapper, requestBody, HttpHeaders.defaultHttpHeaders().addHeader(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON + ";charset=utf-8"));
        try {
            return toResult((Map) json.deserialize(new StringReader(responseBody)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T service(UrlWrapper urlWrapper, String method, Class<T> clazz, Object... parameters) {
        return json.toJavaObject(this.service(urlWrapper, method, parameters), clazz);
    }

    protected String toRequest(String method, Object[] parameters) {
        Map request = new HashMap();
        request.put("method", method);
        if (parameters != null && parameters.length > 0) {
            request.put("params", json.toJsonObject(Arrays.asList(parameters)));
        }
        return json.toJsonString(request);
    }

    protected Object toResult(Map response) {
        if (response.containsKey("error")) {
            Map error = (Map) response.get("error");
            throw new JsonRpcException((String) error.get("type"), (String) error.get("message"));
        }
        return response.get("result");
    }
}
