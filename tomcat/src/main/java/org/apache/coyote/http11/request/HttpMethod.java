package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.http11.exception.InvalidRequestMethodException;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String input) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(input))
                .findAny()
                .orElseThrow(() -> new InvalidRequestMethodException("지원하지 않는 메서드입니다."));
    }

    public String getValue() {
        return value;
    }
}
