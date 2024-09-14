package org.apache.coyote.http;

import static org.apache.coyote.http.HttpCookie.JSESSIONID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpHeaders implements HttpComponent {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final String SEPARATOR = ": ";

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE = "Cookie";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    public HttpHeaders() {
        headers = new ConcurrentHashMap<>();
    }

    public HttpHeaders(final String httpRequest) {
        this();
        String[] lines = httpRequest.split(LINE_FEED);
        int i = 1;
        while ((i < lines.length) && !lines[i].isEmpty()) {
            String[] headerLine = lines[i].split(SEPARATOR);
            headers.put(headerLine[NAME_INDEX], headerLine[VALUE_INDEX]);
            i++;
        }
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public HttpCookies getCookies() {
        return new HttpCookies(headers.get(COOKIE));
    }

    public HttpCookie getCookie(final String name) {
        HttpCookies cookies = getCookies();
        if (cookies != null) {
            return cookies.get(name);
        }
        return null;
    }

    public void setCookie(final HttpCookie cookie) {
        headers.put(SET_COOKIE, cookie.asString());
    }

    public String getSessionId() {
        HttpCookie cookie = getCookie(JSESSIONID);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public void setContentType(final String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void setContentLength(final int contentLength) {
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setLocation(final String location) {
        headers.put(LOCATION, location);
    }

    @Override
    public String asString() {
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            result.append(key)
                    .append(SEPARATOR)
                    .append(value)
                    .append(SPACE)
                    .append(LINE_FEED);
        }
        return result.toString();
    }
}
