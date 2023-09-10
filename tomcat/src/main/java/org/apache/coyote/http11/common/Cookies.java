package org.apache.coyote.http11.common;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private final Map<String, String> values;

    public Cookies() {
        values = new HashMap<>();
    }

    private Cookies(final Map<String, String> values) {
        this.values = values;
    }

    public static Cookies from(final String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return new Cookies();
        }

        final Map<String, String> values = Arrays.stream(cookieHeader.split("; "))
                .map(field -> {
                    final String[] split = field.split("=");
                    return new AbstractMap.SimpleEntry<>(split[0], split[1]);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        return new Cookies(values);
    }

    public static String ofSingleHeader(final String name, final String value) {
        return String.join("=", name, value);
    }

    public String findByName(final String name) {
        return values.get(name);
    }

}
