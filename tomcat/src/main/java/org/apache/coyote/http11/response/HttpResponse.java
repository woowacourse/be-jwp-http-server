package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private final StatusCode statusCode;
    private final Map<HttpResponseHeader, String> headers;
    private final String body;

    public HttpResponse(final StatusCode statusCode) {
        this.httpVersion = HTTP_VERSION;
        this.statusCode = statusCode;
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    public HttpResponse(final StatusCode statusCode, final String body) {
        this.httpVersion = HTTP_VERSION;
        this.statusCode = statusCode;
        this.headers = new LinkedHashMap<>();
        this.body = body;
    }

    public void addHeader(final HttpResponseHeader header, final String value) {
        headers.put(header, value);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Map<HttpResponseHeader, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
