package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.response.ResponseHeaderKey.SET_COOKIE;

public class Cookie {

    private static final String COOKIE_DELIMITER = ";";
    public static final String COOKIE_VALUE_DELIMITER = "=";
    public static final int COOKIE_KEY_INDEX = 0;
    public static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> cookies;

    private Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Cookie() {
        this.cookies = new HashMap<>();
    }

    public static Cookie from(String cookieValue) {
        if (cookieValue.isBlank()) {
            return new Cookie(new HashMap<>());
        }

        Map<String, String> cookies = Arrays.stream(cookieValue.split(COOKIE_DELIMITER))
                .map(cookieElement -> cookieElement.trim().split(COOKIE_VALUE_DELIMITER))
                .collect(Collectors.toMap(cookie -> cookie[COOKIE_KEY_INDEX], cookie -> cookie[COOKIE_VALUE_INDEX]));
        return new Cookie(cookies);
    }

    public String findByKey(String key) {
        return cookies.get(key);
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public String generateResponseMessage() {
        String cookieValue = cookies.entrySet().stream()
                .map(cookieEntry -> cookieEntry.getKey() + COOKIE_VALUE_DELIMITER + cookieEntry.getValue())
                .collect(Collectors.joining(COOKIE_DELIMITER));

        return String.format("%s: %s ", SET_COOKIE.getResponseHeaderName(), cookieValue);
    }
}
