package com.techcourse.servlet.mapper;


import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.UserController;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.HttpRequest;

public class RequestMapping {

    private final Map<RequestValue, Controller> mappings = new HashMap<>();

    public RequestMapping() {
        registerController(HttpMethod.GET, "/login", new LoginController());
        registerController(HttpMethod.POST, "/login", new LoginController());
        registerController(HttpMethod.GET, "/register", new UserController());
        registerController(HttpMethod.POST, "/register", new UserController());
        registerController(HttpMethod.GET, "/", new HomeController());
    }

    private void registerController(HttpMethod method, String path, Controller controller) {
        mappings.put(new RequestValue(method, path), controller);
    }

    public Controller getController(HttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getTargetPath();
        return mappings.get(new RequestValue(method, path));
    }
}
