package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String requestUri;
    private final String path;
    private final String protocol;
    private final QueryString queryString;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(final List<String> request) {
        final String[] firstLine = request.get(0).split(" ");
        this.method = HttpMethod.of(firstLine[0]);
        this.requestUri = firstLine[1];
        this.protocol = firstLine[2];
        final List<String> requestTokens = List.of(requestUri.split("\\?"));
        this.path = requestTokens.get(0);
        this.queryString = QueryString.of(requestTokens);
        request.stream()
                .skip(1)
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split(": "))
                .forEach(line -> headers.put(line[0], line[1]));
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", requestUri='" + requestUri + '\'' +
                ", path='" + path + '\'' +
                ", protocol='" + protocol + '\'' +
                ", queryString=" + queryString +
                ", headers=" + headers +
                '}';
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getPath() {
        return path;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public ContentType getContentType() {
        return ContentType.of(headers.getOrDefault("Content-Type", ""));
    }
}
