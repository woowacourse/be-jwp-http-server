package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomePageController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final RequestMapping instance = new RequestMapping();
    private static final Map<String, Controller> controllers = new ConcurrentHashMap<>();
    private static final Controller staticResourceController = StaticResourceController.getInstance();
    private static final Controller homePageController = HomePageController.getInstance();
    private static final String PATH_SEPARATOR = "/";
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final int PATH_FIRST_SEGMENT_INDEX = 1;

    static {
        controllers.put("login", LoginController.getInstance());
        controllers.put("register", RegisterController.getInstance());
    }

    private RequestMapping() {
    }

    public static RequestMapping getInstance() {
        return instance;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();
        if (PATH_SEPARATOR.equals(path)) {
            return homePageController;
        }
        if (path.contains(FILE_EXTENSION_SEPARATOR)) {
            return staticResourceController;
        }
        return controllers.get(path.split(PATH_SEPARATOR)[PATH_FIRST_SEGMENT_INDEX]);
    }
}
