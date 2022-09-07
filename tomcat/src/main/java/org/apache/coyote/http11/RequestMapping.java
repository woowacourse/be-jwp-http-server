package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.handler.Controller;
import nextstep.jwp.handler.DefaultController;
import nextstep.jwp.handler.ErrorController;
import nextstep.jwp.handler.FileController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;

public class RequestMapping {

    private static final String FILE_CONTROLLER_KEY = "file";
    private static final String ERROR_CONTROLLER_KEY = "error";

    private static final Map<String, Controller> values = new HashMap<>();

    static {
        values.put("/", DefaultController.getInstance());
        values.put("/login", LoginController.getInstance());
        values.put("/register", RegisterController.getInstance());
        values.put(FILE_CONTROLLER_KEY, FileController.getInstance());
        values.put(ERROR_CONTROLLER_KEY, ErrorController.getInstance());
    }

    private RequestMapping() {
    }

    public static Controller of(final String url) {
        final Controller controller = values.get(url);

        if (Objects.nonNull(controller)) {
            return controller;
        }

        if (url.contains(".")) {
            return values.get(FILE_CONTROLLER_KEY);
        }

        return values.get(ERROR_CONTROLLER_KEY);
    }
}
