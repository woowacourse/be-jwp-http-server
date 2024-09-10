package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> pathControllerMap;

    private final Controller basicController;

    public RequestMapping(Controller basicController) {
        this.pathControllerMap = new HashMap<>();
        this.basicController = basicController;
    }

    public void putController(String requestUri, Controller controller) {
        pathControllerMap.put(requestUri, controller);
    }

    public Controller findController(String requestUri) {
        return pathControllerMap.getOrDefault(requestUri, basicController);
    }
}
