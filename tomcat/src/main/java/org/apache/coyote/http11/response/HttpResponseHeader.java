package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private Map<String, String> httpResponseHeaders;

    public HttpResponseHeader() {
        this.httpResponseHeaders = new HashMap<>();
    }

    public void add(String key, String value) {
        this.httpResponseHeaders.put(key, value);
    }

    public String get(String key) {
        return httpResponseHeaders.get(key);
    }
}
