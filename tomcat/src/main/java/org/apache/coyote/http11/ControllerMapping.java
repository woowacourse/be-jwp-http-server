package org.apache.coyote.http11;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.GreetingController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.support.ResourceSuffix;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpMethod.GET;

public class ControllerMapping {

    private final Map<RequestInfo, Controller> mapping = new HashMap<>();

    public ControllerMapping() {
        mapping.put(new RequestInfo(GET, "/"), new GreetingController());
        mapping.put(new RequestInfo(GET, "/login"), new LoginController());
    }

    public Controller getController(final RequestInfo requestInfo) {
        final Controller controller = mapping.get(requestInfo);
        if (controller != null) {
            return controller;
        }
        if (isResourceUri(requestInfo.getUri())) {
            return new ResourceController();
        }
        throw new CustomNotFoundException(requestInfo.getUri() + "를 처리할 컨트롤러를 찾지 못함");
    }

    private boolean isResourceUri(final String uri) {
        return ResourceSuffix.isEndWith(uri);
    }
}
