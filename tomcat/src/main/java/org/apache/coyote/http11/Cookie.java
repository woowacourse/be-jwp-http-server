package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookie {

    private static final String PAIR_DELIMITER = "=";
    private static final String COOKIE_DELIMITER = ";";

    private final Map<String, String> cookie = new HashMap<>();

    public Cookie(String cookie) {
        if (cookie == null) {
            cookie = "";
        }

        parseCookie(cookie);
    }

    private void parseCookie(String cookie) {
        String[] pairs = cookie.split(COOKIE_DELIMITER);

        for (String pair : pairs) {
            if (pair.contains(PAIR_DELIMITER)) {
                String[] keyValuePair = pair.trim().split(PAIR_DELIMITER);
                putIfValidPair(keyValuePair);
            }
        }
    }

    private void putIfValidPair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            return;
        }
        String key = keyValuePair[0].trim();
        String value = keyValuePair[1].trim();

        cookie.put(key, value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(cookie.get(key));
    }
}