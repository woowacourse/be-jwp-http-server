package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HttpMethod 입니다."));
    }
}
