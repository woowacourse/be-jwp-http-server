package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HeaderType;
import org.apache.coyote.http11.exception.BadRequestException;

public class HttpResponseHeaders {

    private static final String HEADER_FORMAT = "%s: %s ";

    private final Map<String, String> headers;

    private HttpResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders createEmptyHeaders() {
        return new HttpResponseHeaders(new LinkedHashMap<>());
    }

    public void addHeader(String key, String value) {
        validateKey(key);
        validateValue(value);

        headers.put(key, value);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new BadRequestException("Header Key is Null");
        }
    }

    private void validateValue(String value) {
        if (value == null) {
            throw new BadRequestException("Header Value is Null");
        }
    }

    public void setContentType(String value) {
        validateValue(value);

        headers.put(HeaderType.CONTENT_TYPE.getValue(), value);
    }

    public void setContentLength(String value) {
        validateValue(value);

        addHeader(HeaderType.CONTENT_LENGTH.getValue(), value);
    }

    public void setLocation(String value) {
        validateValue(value);

        headers.put(HeaderType.LOCATION.getValue(), value);
    }

    public void clear() {
        headers.clear();
    }

    public String getHeaders() {
        return headers.keySet()
                .stream()
                .map(key -> String.format(HEADER_FORMAT, key, headers.get(key)))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String get(String key) {
        validateKey(key);

        return headers.get(key);
    }

}
