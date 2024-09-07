package org.apache.coyote.controller;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.LoginPageController;
import com.techcourse.controller.NotFoundController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RegisterPageController;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestKey;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.util.FileExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);
    private final Map<RequestKey, Controller> controllers = new HashMap<>();

    public FrontController() {
        controllers.put(new RequestKey(HttpMethod.GET, "/login"), new LoginPageController());
        controllers.put(new RequestKey(HttpMethod.POST, "/login"), new LoginController());
        controllers.put(new RequestKey(HttpMethod.GET, "/register"), new RegisterPageController());
        controllers.put(new RequestKey(HttpMethod.POST, "/register"), new RegisterController());
    }

    public HttpResponse dispatch(HttpRequest request, Manager manager) {
        log(request);
        String path = request.getPath();
        if (FileExtension.isFileExtension(path)) {
            String resourcePath = "static" + path;
            try {
                Path filePath = Paths.get(getClass().getClassLoader().getResource(resourcePath).toURI());
                MimeType mimeType = MimeType.from(FileExtension.from(path));
                byte[] body = Files.readAllBytes(filePath);
                ResponseHeader header = new ResponseHeader();
                header.setContentType(mimeType);
                return new HttpResponse(HttpStatusCode.OK, header, body);
            } catch (URISyntaxException | IOException e) {
                ResponseHeader header = new ResponseHeader();
                header.setContentType(MimeType.OTHER);
                return new HttpResponse(HttpStatusCode.OK, header, "No File Found".getBytes());
            }
        }
        Controller controller = getController(request.getMethod(), path);
        return controller.service(request, manager);
    }

    private void log(HttpRequest request) {
        log.info("method = {}, path = {}, url = {}", request.getMethod(), request.getPath(), request.getUrl());
        for (String key : request.getQueryParams().keySet()) {
            log.info("key = {}, value = {}", key, request.getQueryParams().get(key));
        }
        for (String key : request.getHeaders().getCookies().getCookies().keySet()) {
            log.info("COOKIE: key = {}, value = {}", key, request.getHeaders().getCookies().getCookies().get(key));
        }
        if (!request.isBodyEmpty()) {
            log.info(request.getBody());
        }
    }

    public Controller getController(HttpMethod method, String path) {
        Controller controller = controllers.get(new RequestKey(method, path));
        if (controller == null) {
            return new NotFoundController();
        }
        return controller;
    }
}
