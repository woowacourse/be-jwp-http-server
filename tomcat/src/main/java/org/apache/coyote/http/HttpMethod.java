package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    CONNECT("CONNECT"),
    ;

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static boolean isHttpMethod(String method) {
        return Arrays.stream(HttpMethod.values()).anyMatch(httpMethod -> httpMethod.getMethod().equals(method));
    }

    public static HttpMethod findByMethod(String method) {
        return Arrays.stream(HttpMethod.values()).filter(httpMethod -> httpMethod.getMethod().equals(method)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown http method: " + method));
    }
}
