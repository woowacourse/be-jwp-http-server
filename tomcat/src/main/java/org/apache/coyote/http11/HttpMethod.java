package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod of(final String value) {
        return Arrays.stream(HttpMethod.values())
                .findFirst()
                .filter(it -> it.value.equalsIgnoreCase(value))
                .orElseThrow();
    }

    public boolean isGet() {
        return this == GET;
    }
}
