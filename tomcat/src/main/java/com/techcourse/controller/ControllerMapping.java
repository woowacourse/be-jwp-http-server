package com.techcourse.controller;

import com.techcourse.controller.dto.Response;
import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.HttpMethod;

public enum ControllerMapping {

    SEARCH_USER(HttpMethod.GET, "/login", params -> new UserController().searchUserData(params));

    private final HttpMethod httpMethod;
    private final String path;
    private final Function<Map<String, String>, Response<?>> handler;

    ControllerMapping(HttpMethod httpMethod, String path, Function<Map<String, String>, Response<?>> handler) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.handler = handler;
    }

    public static ControllerMapping of(HttpMethod httpMethod, String path) {
        return Arrays.stream(values())
                .filter(mapping -> mapping.httpMethod.equals(httpMethod) && mapping.path.equals(path))
                .findFirst()
                .orElseThrow(
                        () -> new UncheckedServletException(new IllegalArgumentException("처리할 수 있는 핸들러가 존재하지 않습니다."))
                );
    }

    public Response<?> apply(Map<String, String> params) {
        return handler.apply(params);
    }
}
