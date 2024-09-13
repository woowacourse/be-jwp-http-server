package org.apache.coyote.http11.httprequest;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaderName;

public class HttpRequestHeader {

    private static final String BODY_FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    public HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    public boolean containsHeader(HttpHeaderName httpHeaderName) {
        return headers.containsKey(httpHeaderName.getName());
    }

    public boolean existRequestBody() {
        return getHeaderValue(HttpHeaderName.CONTENT_TYPE).equals(BODY_FORM_CONTENT_TYPE);
    }

    public String getHeaderValue(String key) {
        return headers.get(key);
    }

    public String getHeaderValue(HttpHeaderName httpHeaderName) {
        return headers.get(httpHeaderName.getName());
    }
}
