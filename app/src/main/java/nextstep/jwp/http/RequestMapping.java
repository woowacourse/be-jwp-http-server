package nextstep.jwp.http;

import nextstep.jwp.controller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Logger logger = LoggerFactory.getLogger(RequestMapping.class);
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new DefaultController());
        controllers.put("/register", new RegisterController());
        controllers.put("/login", new LoginController());
        controllers.put("RESOURCE", new ResourceController());
    }

    private RequestMapping() {
    }

    public static Controller findController(String inputPath) {
        Controller controller = controllers.keySet().stream()
                .filter(path -> path.equals(inputPath))
                .findAny()
                .map(controllers::get)
                .orElseGet(NotFoundController::new);
        logger.info("Found {}", controller.getClass());
        return controller;
    }
}
