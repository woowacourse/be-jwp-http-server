package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Http11RequestHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headerMap;

    public Http11RequestHeaders(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static Http11RequestHeaders from(String headerLines) {
        Map<String, String> map = new HashMap<>();

        for (String headerLine : headerLines.split("\r\n")) {
            int index = headerLine.indexOf(":");
            assert index != -1;
            String key = headerLine.substring(0, index);
            String value = headerLine.substring(index + 1);
            assert !key.isBlank();
            assert !value.isBlank();
            map.put(key.trim(), value.trim());
        }

        return new Http11RequestHeaders(map);
    }

    public String get(String key) {
        return headerMap.get(key);
    }

    public boolean contains(String key) {
        return headerMap.containsKey(key);
    }

    public int getContentLength() {
        if (!headerMap.containsKey(CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt(headerMap.get(CONTENT_LENGTH));
    }
}
