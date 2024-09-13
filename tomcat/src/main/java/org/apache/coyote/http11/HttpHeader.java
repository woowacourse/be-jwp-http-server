package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.response.ContentType;

public class HttpHeader {

    private static final String RESPONSE_HEADER_FORMAT = "%s: %s\r\n";
    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>();
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHttpHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((key, value) -> stringBuilder.append(String.format(RESPONSE_HEADER_FORMAT, key, value)));
        return stringBuilder.toString();
    }

    public void putContentType(String type) {
        headers.put("Content-Type", ContentType.getContentType(type));
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public String getCookie() {
        return headers.get("Cookie");
    }
}
