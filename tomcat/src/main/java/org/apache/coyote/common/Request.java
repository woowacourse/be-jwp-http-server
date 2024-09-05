package org.apache.coyote.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final Cookie cookie;
    private final String body;

    public Request(String method, String uri, String protocol, String[] headers, Cookie cookie, String body) {
        this.method = method;
        this.uri = parseUri(uri);
        this.protocol = protocol;
        this.headers = parseHeaders(headers);
        this.cookie = parseCookie();
        this.body = body;
        this.parameters = parseParameters(uri);
    }

    public Request(String method, String uri, String protocol, String[] headers, String body) {
        this(method, uri, protocol, headers, Cookie.empty(), body);
    }

    private String parseUri(String uri) {
        if (!uri.contains("?")) {
            return uri;
        }
        return uri.substring(0, uri.indexOf("?"));
    }

    private Map<String, String> parseHeaders(String[] headers) {
        Map<String, String> result = new HashMap<>();
        for (String header : headers) {
            String[] token = header.split(": ");
            result.put(token[0], token[1]);
        }
        return result;
    }

    private Cookie parseCookie() {
        if (headers.containsKey("Cookie")) {
            String cookies = headers.get("Cookie");
            HashMap<String, String> collect = Arrays.stream(cookies.split("; "))
                    .map(cookie -> cookie.split("="))
                    .collect(HashMap::new, (map, entry) -> map.put(entry[0], entry[1]), HashMap::putAll);
            return new Cookie(collect);
        }
        return Cookie.empty();
    }

    private Map<String, String> parseParameters(String uri) {
        if (!uri.contains("?")) {
            return new HashMap<>();
        }
        return Arrays.stream(uri.substring(uri.indexOf("?") + 1).split("&"))
                .map(param -> param.split("="))
                .collect(HashMap::new, (map, entry) -> map.put(entry[0], entry[1]), HashMap::putAll);
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
               "method='" + method + '\'' +
               ", uri='" + uri + '\'' +
               ", protocol='" + protocol + '\'' +
               ", headers=" + headers +
               ", parameters=" + parameters +
               ", cookie=" + cookie +
               ", body='" + body + '\'' +
               '}';
    }
}
