package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpMessageBodyInfo;

public class HttpResponseHeader {

    private final Map<String, List<String>> values;
    private final Cookie cookie;

    public HttpResponseHeader() {
        this.values = new LinkedHashMap<>();
        this.cookie = new Cookie();
    }

    public void add(String name, String value) {
        if (HttpMessageBodyInfo.isContentType(name)) {
            addContentType(value);
        }
        if (HttpMessageBodyInfo.isContentLength(name)) {
            addContentLength(value.getBytes().length);
        }
        this.values.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    public void addCookie(String key, String value) {
        cookie.add(key, value);
        values.computeIfAbsent(HttpMessageBodyInfo.SET_COOKIE.getValue(), k -> new ArrayList<>()).add(key + "=" + value);
    }

    public void addContentType(String contentType) {
        if (contentType.equals("text/html")) {
            values.put(HttpMessageBodyInfo.CONTENT_TYPE.getValue(), List.of(contentType + ";charset=utf-8"));
            return;
        }
        values.put(HttpMessageBodyInfo.CONTENT_TYPE.getValue(), List.of(contentType));
    }

    public void addContentLength(int length) {
        values.put(HttpMessageBodyInfo.CONTENT_LENGTH.getValue(), List.of(String.valueOf(length)));
    }

    public void addLocation(String redirectUri) {
        values.put(HttpMessageBodyInfo.LOCATION.getValue(), List.of(redirectUri));
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(";", entry.getValue()) + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
