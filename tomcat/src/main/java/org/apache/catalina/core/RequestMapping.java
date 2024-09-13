package org.apache.catalina.core;

import com.techcourse.controller.DashBoardController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import org.apache.catalina.connector.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.Controller;

public class RequestMapping {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final Controller staticResourceController = new StaticResourceController();

    static {
        controllers.put("/", new DashBoardController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
    }

    public Controller getController(HttpRequest request) {
        Controller controller = controllers.get(request.getPath());
        if (controller == null) {
            return staticResourceController;
        }
        return controller;
    }
}
