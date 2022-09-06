package org.apache.coyote.http11.model;

import static org.apache.coyote.http11.model.StringFormat.CRLF;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    private Headers(final Map<String, String> headers, final HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static Headers empty() {
        return new Headers(new LinkedHashMap<>(), new HttpCookie(new HashMap<>()));
    }

    public static Headers of(final List<String> headerLines) {
        Map<String, String> headers = extract(headerLines);

        if (hasCookie(headers)) {
            String cookieLine = headers.remove(Header.COOKIE.getKey());
            return new Headers(headers, HttpCookie.of(cookieLine));
        }
        return new Headers(headers, HttpCookie.empty());
    }

    private static Map<String, String> extract(final List<String> headerLines) {
        return headerLines.stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    private static boolean hasCookie(final Map<String, String> headers) {
        return headers.containsKey(Header.COOKIE.getKey());
    }

    public void add(final String key, final String value) {
        this.headers.put(key, value);
    }

    public String getValue(final String key) {
        return headers.get(key);
    }

    public HttpCookie getCookie() {
        return this.cookie;
    }

    public String getString() {
        List<String> headerLines = headers.entrySet()
                .stream()
                .map(keyValue -> String.join(": ",
                        keyValue.getKey(),
                        keyValue.getValue() + " ")
                ).collect(Collectors.toList());

        return String.join(CRLF, headerLines);
    }
}
