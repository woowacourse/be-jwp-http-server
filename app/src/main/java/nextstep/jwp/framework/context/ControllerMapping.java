package nextstep.jwp.framework.context;

import java.util.ArrayList;
import java.util.List;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.webserver.controller.Controller;
import nextstep.jwp.webserver.controller.ErrorController;
import nextstep.jwp.webserver.controller.IndexPageController;
import nextstep.jwp.webserver.controller.WelcomePageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMapping.class);

    private static final List<Controller> CONTROLLERS = new ArrayList<>();

    static {
        CONTROLLERS.add(new WelcomePageController());
        CONTROLLERS.add(new IndexPageController());
        CONTROLLERS.add(new ErrorController());
    }

    public static Controller findController(HttpRequest httpRequest) {
        final Controller foundController = CONTROLLERS.stream()
                                                      .filter(controller -> controller.canHandle(httpRequest))
                                                      .findAny()
                                                      .orElse(ErrorController.INSTANCE);

        LOGGER.debug("found controller : {}", foundController.getClass().getName());

        return foundController;
    }
}
