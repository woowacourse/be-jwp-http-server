package com.techcourse.controller;

import java.util.Map;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.MediaType;

public class RequestMapping implements Controller {

    private static final RequestMapping INSTANCE = new RequestMapping();

    private final Map<String, Controller> controllers;

    public RequestMapping() {
        controllers = Map.of(
                "/", new RootController(),
                "/login", new LoginController(),
                "/register", new RegisterController()
        );
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        String path = request.getPath();
        if (MediaType.isSupportedExtension(path)) {
            new StaticResourceHandler().service(request, response);
            return;
        }
        Controller controller = controllers.get(path);
        if (controller == null) {
            controller = new NotFoundController();
        }
        controller.service(request, response);
    }
}
