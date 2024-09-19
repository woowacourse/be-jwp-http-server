package org.apache.coyote.http11;

import com.techcourse.servlet.IndexServlet;
import com.techcourse.servlet.LoginServlet;
import com.techcourse.servlet.RegisterServlet;
import com.techcourse.servlet.WelcomePageServlet;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.resource.ResourceParser;

public class FrontServlet implements Servlet {

    private static final FrontServlet INSTANCE = new FrontServlet();

    private final Map<String, Servlet> controllerMap;

    private FrontServlet() {
        Map<String, Servlet> controllerMap = new HashMap<>();
        controllerMap.put("/", new WelcomePageServlet());
        controllerMap.put("/index", new IndexServlet());
        controllerMap.put("/login", new LoginServlet());
        controllerMap.put("/register", new RegisterServlet());

        this.controllerMap = controllerMap;
    }

    public static FrontServlet getInstance() {
        return INSTANCE;
    }

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        try {
            if (isResourceRequest(req.getPath())) {
                File file = ResourceParser.getRequestFile(req.getPath());
                resp.setResponse(HttpStatus.OK, file);
                return;
            }

            Servlet servlet = controllerMap.get(req.getPath());
            servlet.service(req, resp);
        } catch (NullPointerException e) {
            resp.setResponse(HttpStatus.NOT_FOUND, "/404.html");
        }
    }

    public boolean isResourceRequest(String path) {
        int extensionIndex = path.lastIndexOf(".");
        if (extensionIndex == -1) {
            return false;
        }
        String extension = path.substring(extensionIndex);
        return !extension.isEmpty();
    }
}
