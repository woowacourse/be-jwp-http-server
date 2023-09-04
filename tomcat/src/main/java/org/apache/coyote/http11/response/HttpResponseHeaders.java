package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeaders {
    private final Map<ResponseHeaderType, String> headers;

    public HttpResponseHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpResponseHeaders(final Map<ResponseHeaderType, String> headers) {
        this.headers = headers;
    }

    public void add(ResponseHeaderType headerType, String value) {
        headers.put(headerType, value);
    }

    @Override
    public String toString() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s ", entry.getKey().getType(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
