package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.cookie.RequestCookies;
import org.apache.coyote.http11.exception.HttpFormatException;

public class HttpHeaders {

    private static final String HEADER_SEPARATOR = ": ";
    private static final int SPLIT_HEADER_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;
    private final RequestCookies cookies;

    public HttpHeaders(Map<String, String> values) {
        this.values = values;
        this.cookies = RequestCookies.of(values.get(HttpHeader.COOKIE));
    }

    public static HttpHeaders of(List<String> headers) {
        Map<String, String> map = new HashMap<>();
        for (String header : headers) {
            if (!isValidHeader(header)) {
                throw new HttpFormatException("올바르지 않은 헤더 형식입니다.");
            }
            String[] splitHeader = header.split(HEADER_SEPARATOR);
            map.put(splitHeader[KEY_INDEX], splitHeader[VALUE_INDEX]);
        }
        return new HttpHeaders(map);
    }

    private static boolean isValidHeader(String header) {
        List<String> splitHeader = Arrays.stream(header.split(HEADER_SEPARATOR)).toList();
        if (splitHeader.size() != SPLIT_HEADER_SIZE) {
            return false;
        }
        return splitHeader.stream().noneMatch(String::isBlank);
    }

    public String get(String key) {
        return values.get(key);
    }

    public OptionalInt getAsInt(String key) {
        String value = get(key);
        if (value == null) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public String getSessionId() {
        return cookies.getSessionId();
    }

    public String getCookie(String cookieName) {
        return cookies.get(cookieName);
    }

    public void addSession(String sessionId) {
        cookies.addSession(sessionId);
    }
}
