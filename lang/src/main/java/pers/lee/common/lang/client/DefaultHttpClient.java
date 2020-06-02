package pers.lee.common.lang.client;

import okhttp3.*;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Passyt on 2018/5/18.
 */
public class DefaultHttpClient implements HttpClient {

    private final OkHttpClient client;
    private final Set<Integer> extraSuccessResponseCodes;

    public DefaultHttpClient(OkHttpClient client) {
        this(client, null);
    }

    public DefaultHttpClient(OkHttpClient client, Set<Integer> extraSuccessResponseCodes) {
        this.client = client;
        this.extraSuccessResponseCodes = extraSuccessResponseCodes;
    }

    @Override
    public String execute(UrlWrapper urlWrapper, String method, String body, HttpHeaders headers) {
        try {
            RequestBody requestBody = null;
            String contentType = getContentType(body, headers.get(HttpHeaders.CONTENT_TYPE));
            if (body != null) {
                requestBody = RequestBody.create(MediaType.parse(contentType), body);
            }
            Request.Builder builder = new Request.Builder().url(urlWrapper.getUrl()).method(method, requestBody).addHeader("url.alias", urlWrapper.getAlias());
            headers.entrySet().forEach(e -> builder.addHeader(e.getKey(), e.getValue()));
            Request request = builder.build();
            try (Response response = client.newCall(request).execute()) {
                if (!isSuccessful(response)) {
                    throw new HttpInvocationException(urlWrapper.getUrl(), response.code(), response.body().string());
                }

                return response.body().string();
            }
        } catch (IOException e) {
            throw new HttpInvocationException(urlWrapper.getUrl(), e);
        }
    }

    protected String getContentType(String body, String contentType) {
        if (contentType != null && contentType.length() > 0) {
            return contentType;
        }

        if (body == null) {
            return null;
        }

        if (body.startsWith("<")) {
            return HttpHeaders.CONTENT_TYPE_XML;
        } else if (body.startsWith("\"") || body.startsWith("[")) {
            return HttpHeaders.CONTENT_TYPE_JSON;
        }
        return "text/plain";
    }

    boolean isSuccessful(Response response) {
        if (extraSuccessResponseCodes != null && extraSuccessResponseCodes.size() > 0 && extraSuccessResponseCodes.contains(response.code())) {
            return true;
        }
        return response.isSuccessful();
    }
}
