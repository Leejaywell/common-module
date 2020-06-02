package pers.lee.common.lang.web;

import pers.lee.common.lang.ssl.AlwaysTrustSSLSocketFactory;
import pers.lee.common.lang.client.HttpHeaders;
import pers.lee.common.lang.json.Json;
import pers.lee.common.lang.ssl.AlwaysTrustHostnameVerifier;
import pers.lee.common.lang.ssl.TrustSSL4CertsSocketFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Passyt on 2018/5/16.
 */
public class HttpConnectionRedirectService {

    public static final String REDIRECT_URL_KEY = "Redirect-URL";
    public static final String REDIRECT_METHOD = "Redirect-Method";
    public static final String REDIRECT_HEADERS = "Redirect-Headers";
    public static final String REDIRECT_HEADERS_EXTRA = "Redirect-Headers-Extra";
    public static final String REDIRECT_CERT = "Redirect-Cert";
    public static final String REDIRECT_AUTH = "Redirect-Auth";
    public static final String REDIRECT_BODY = "Redirect-Body";
    public static final String REDIRECT_PREFIX = "Redirect";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String TYPE = "type";
    public static final String CERT = "cert";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final int DEFAULT_CONNECT_TIMEOUT = 5 * 1000;
    public static final int DEFAULT_READ_TIMEOUT = 300 * 1000;

    public static final String AUTHENTICATION_BASIC = "basic";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";

    private static final Logger log = LoggerFactory.getLogger(HttpConnectionRedirectService.class);

    public void execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String redirectURL = getRedirectURL(httpServletRequest);
        String redirectMethod = getRedirectMethod(httpServletRequest);

        if (log.isInfoEnabled()) {
            log.info("redirect to [" + redirectURL + "] of [" + redirectMethod + "]; ");
        }

        HttpURLConnection connection = null;
        try {
            httpServletRequest.setCharacterEncoding("UTF-8");
            httpServletResponse.setCharacterEncoding("UTF-8");

            connection = (HttpURLConnection) new URL(redirectURL).openConnection();
            initConnection(connection, httpServletRequest, redirectMethod);
            if (connection instanceof HttpsURLConnection) {
                initHttpsURLConnection(HttpsURLConnection.class.cast(connection), httpServletRequest);
            }

            if ("POST".equalsIgnoreCase(redirectMethod) || "PUT".equalsIgnoreCase(redirectMethod)) {
                String redirectBody = httpServletRequest.getParameter(REDIRECT_BODY);
                try (OutputStream redirectOutput = connection.getOutputStream()) {
                    if (hasRedirectBody(httpServletRequest)) {
                        IOUtils.copy(new ByteArrayInputStream(redirectBody.getBytes(StandardCharsets.UTF_8)), redirectOutput);
                    } else if (isFormSubmit(httpServletRequest)) {
                        Map<String, Object> paramters = getValidParameters(httpServletRequest);
                        String queryString = getQueryString(paramters, StandardCharsets.UTF_8);
                        IOUtils.copy(new ByteArrayInputStream(queryString.getBytes(StandardCharsets.UTF_8)), redirectOutput);
                    } else {
                        IOUtils.copy(httpServletRequest.getInputStream(), redirectOutput);
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            httpServletResponse.setStatus(responseCode);
            connection.getHeaderFields().entrySet().stream()
                    .filter(e -> !HEADER_TRANSFER_ENCODING.equals(e.getKey()) && !HEADER_CONTENT_ENCODING.equals(e.getKey()))
                    .flatMap(e -> e.getValue().stream()
                            .map(s -> new AbstractMap.SimpleImmutableEntry(e.getKey(), s)))
                    .forEach(entry -> httpServletResponse.setHeader((String) entry.getKey(), (String) entry.getValue()));

            try (InputStream redirectInput = connection.getInputStream()) {
                IOUtils.copy(redirectInput, httpServletResponse.getOutputStream());
            }
        } catch (Exception e) {
            log.error("redirect to [" + redirectURL + "] fail", e);
            throw new RuntimeException("redirect to [" + redirectURL + "] fail", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected void initConnection(HttpURLConnection connection, HttpServletRequest httpServletRequest, String redirectMethod) throws ProtocolException {
        connection.setRequestMethod(redirectMethod);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        getRedirectHeaders(httpServletRequest).forEach((key, value) -> connection.setRequestProperty(key, value));
    }

    protected void initHttpsURLConnection(HttpsURLConnection connection, HttpServletRequest httpServletRequest) {
        connection.setSSLSocketFactory(getSSLSocketFactory(httpServletRequest));
        connection.setHostnameVerifier(new AlwaysTrustHostnameVerifier());
    }

    protected String getRedirectURL(HttpServletRequest httpServletRequest) {
        String redirectUrl = httpServletRequest.getHeader(REDIRECT_URL_KEY);
        if (redirectUrl == null) {
            redirectUrl = httpServletRequest.getParameter(REDIRECT_URL_KEY);
            try {
                redirectUrl = URLDecoder.decode(redirectUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("decode url failed", e);
            }
        }
        return redirectUrl;
    }

    protected String getRedirectMethod(HttpServletRequest httpServletRequest) {
        String method = httpServletRequest.getHeader(REDIRECT_METHOD);
        if (method == null) {
            method = httpServletRequest.getParameter(REDIRECT_METHOD);
        }
        if (method == null) {
            method = httpServletRequest.getMethod();
        }
        return method.toUpperCase();
    }

    protected Map<String, String> getRedirectHeaders(HttpServletRequest httpServletRequest) {
        Map<String, String> allHeaders = new HashMap<>();

        String headerValueString = httpServletRequest.getHeader(REDIRECT_HEADERS);
        if (headerValueString == null) {
            headerValueString = httpServletRequest.getParameter(REDIRECT_HEADERS);
        }
        if (headerValueString != null) {
            try {
                Map<String, String> headers = (Map<String, String>) Json.getDefault().deserialize(new StringReader(headerValueString));
                allHeaders.putAll(headers);
            } catch (Exception e) {
                throw new RuntimeException("Illegal headers format", e);
            }
        }

        Map redirectHeadersExtra = (Map) httpServletRequest.getAttribute(REDIRECT_HEADERS_EXTRA);
        if (redirectHeadersExtra != null) {
            allHeaders.putAll(redirectHeadersExtra);
        }

        String authValueString = httpServletRequest.getHeader(REDIRECT_AUTH);
        if (authValueString == null) {
            authValueString = httpServletRequest.getParameter(REDIRECT_AUTH);
        }
        if (authValueString != null) {
            try {
                Map<String, String> authMap = (Map<String, String>) Json.getDefault().deserialize(new StringReader(headerValueString));
                String type = authMap.get(TYPE);
                if (type == null) {
                    throw new IllegalArgumentException("Missing type of Redirect-Auth");
                }
                if (!AUTHENTICATION_BASIC.equals(type)) {
                    throw new IllegalArgumentException("Wrong type of Redirect-Auth, it should be basic ");
                }
                String username = authMap.get(USERNAME);
                if (username == null) {
                    throw new IllegalArgumentException("Missing username of Redirect-Auth");
                }
                String password = authMap.get(PASSWORD);
                if (password == null) {
                    throw new IllegalArgumentException("Missing password of Redirect-Auth");
                }

                allHeaders.put("Authorization", HttpHeaders.getBasicAuthorization(username, password));
            } catch (Exception e) {
                throw new RuntimeException("Illegal auth format", e);
            }
        }

        if (allHeaders.get(HEADER_CONTENT_TYPE) == null && httpServletRequest.getContentType() != null) {
            allHeaders.put(HEADER_CONTENT_TYPE, httpServletRequest.getContentType());
        }

        return allHeaders;
    }

    public static String getQueryString(Map<String, Object> paramters, Charset charset) throws UnsupportedEncodingException {
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, Object> entry : paramters.entrySet()) {
            String parameterName = entry.getKey();
            Object parameterValue = entry.getValue();
            parameterName = URLEncoder.encode(parameterName, charset.name());
            if (parameterValue instanceof List) {
                for (Object eachParameterValue : (List<?>) parameterValue) {
                    stringBuffer.append("&").append(parameterName).append("=");
                    stringBuffer.append(URLEncoder.encode(eachParameterValue.toString(), charset.name()));
                }
            } else {
                stringBuffer.append("&").append(parameterName).append("=");
                stringBuffer.append(URLEncoder.encode(parameterValue.toString(), charset.name()));
            }
        }
        String parameterString = stringBuffer.toString().substring(1);

        return parameterString;
    }

    protected SSLSocketFactory getSSLSocketFactory(HttpServletRequest httpServletRequest) {
        String certValueString = httpServletRequest.getHeader(REDIRECT_CERT);
        if (certValueString == null) {
            certValueString = httpServletRequest.getParameter(REDIRECT_CERT);
        }
        if (certValueString == null) {
            return new AlwaysTrustSSLSocketFactory();
        }

        try {
            Map<String, String> certMap = (Map<String, String>) Json.getDefault().deserialize(new StringReader(certValueString));
            String type = certMap.get(TYPE);
            if (type == null) {
                throw new IllegalArgumentException("Missing type of Redirect-Cert");
            }
            String cert = certMap.get(CERT);
            if (cert == null) {
                throw new IllegalArgumentException("Missing cert of Redirect-Cert");
            }
            String password = certMap.get(PASSWORD);
            if (password == null) {
                throw new IllegalArgumentException("Missing password of Redirect-Cert");
            }
            return new TrustSSL4CertsSocketFactory(type, cert, password);
        } catch (Exception e) {
            throw new RuntimeException("Illegal auth format", e);
        }
    }

    private static Map<String, Object> getValidParameters(HttpServletRequest httpServletRequest) {
        Map<String, Object> returnParamters = new HashMap<>();
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (StringUtils.isBlank(key) || key.startsWith(REDIRECT_PREFIX) || ArrayUtils.isEmpty(values)) {
                continue;
            }
            if (values.length == 1) {
                returnParamters.put(key, values[0]);
            } else {
                returnParamters.put(key, Arrays.asList(values));
            }
        }
        return returnParamters;
    }

    private static boolean isFormSubmit(HttpServletRequest httpServletRequest) {
        String contentType = httpServletRequest.getContentType();
        return contentType != null && contentType.contains(CONTENT_TYPE_FORM);
    }

    private static boolean hasRedirectBody(HttpServletRequest httpServletRequest) {
        return isFormSubmit(httpServletRequest) && StringUtils.isNotBlank(httpServletRequest.getParameter(REDIRECT_BODY));
    }

}
