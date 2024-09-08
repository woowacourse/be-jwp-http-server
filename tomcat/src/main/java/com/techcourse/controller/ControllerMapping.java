package com.techcourse.controller;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.exception.UncheckedServletException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import org.apache.coyote.http11.component.HttpMethod;

public enum ControllerMapping {

    SEARCH_USER(HttpMethod.GET, URI.create("/login"), params -> new UserController().searchUserData(params)),
    LOGIN_USER(HttpMethod.POST, URI.create("/login"), params -> new UserController().login(params)),
    REGISTER_USER(HttpMethod.POST, URI.create("/register"), params -> new UserController().registerUser(params));

    private final HttpMethod httpMethod;
    private final URI path;
    private final Function<Map<String, String>, HttpResponseEntity<?>> handler;

    ControllerMapping(HttpMethod httpMethod, URI path,
                      Function<Map<String, String>, HttpResponseEntity<?>> handler) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.handler = handler;
    }

    public static ControllerMapping of(HttpMethod httpMethod, URI path) {
        return Arrays.stream(values())
                .filter(mapping -> mapping.httpMethod.equals(httpMethod) && mapping.path.equals(path))
                .findFirst()
                .orElseThrow(
                        () -> new UncheckedServletException(new IllegalArgumentException("처리할 수 있는 핸들러가 존재하지 않습니다."))
                );
    }

    public HttpResponseEntity<?> apply(Map<String, String> params) {
        return handler.apply(params);
    }
}
