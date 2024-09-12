package org.apache.coyote;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.controller.HomeController;
import org.apache.coyote.controller.StaticResourceController;

public class RequestMapping {

    private static final Pattern LOGIN_REGEX = Pattern.compile("^(/login)(\\?([^#\\s]*))?");
    private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile(
            "\\.(html|css|js|jpg|jpeg|png|gif|ico|svg|woff|woff2|ttf|eot)$", Pattern.CASE_INSENSITIVE);

    private static final Map<String, AbstractController> controllers = new HashMap<>();

    static {
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("/", new HomeController());
        controllers.put("/static", new StaticResourceController());
    }

    private RequestMapping() {
    }

    public static AbstractController getController(String path) {
        if (LOGIN_REGEX.matcher(path).matches()) {
            return controllers.get("/login");
        }
        if (STATIC_RESOURCE_PATTERN.matcher(path).find()) {
            return controllers.get("/static");
        }
        return controllers.get(path);
    }
}
