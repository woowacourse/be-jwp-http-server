package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.controller.ErrorController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> map = new ConcurrentHashMap<>();
    private static final String STATIC_CONTROLLER = "staticController";
    private static final String ERROR_CONTROLLER = "errorController";

    public RequestMapping() {
        init();
    }

    private void init() {
        map.put(STATIC_CONTROLLER, new StaticController());
        map.put(ERROR_CONTROLLER, new ErrorController());
        map.put("/login", new LoginController());
        map.put("/register", new RegisterController());
    }

    public Controller getController(final HttpRequest httpRequest) {
        if (httpRequest.isStaticRequest()) {
            return map.get(STATIC_CONTROLLER);
        }
        if (map.containsKey(httpRequest.getPath())) {
            return map.get(httpRequest.getPath());
        }
        return map.get(ERROR_CONTROLLER);
    }
}
