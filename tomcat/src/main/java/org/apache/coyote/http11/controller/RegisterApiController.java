package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;

public class RegisterApiController extends AbstractController {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String responseBody = getBody();

        httpResponse.ok(ContentType.HTML, responseBody);
    }

    private String getBody() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getRequestBody();
        try {
            Map<String, Object> parameters = requestBody.getParameters();
            String account = (String) parameters.get("account");
            String password = (String) parameters.get("password");
            String email = (String) parameters.get("email");

            User user = new User(account, password, email);

            InMemoryUserRepository.save(user);
            SESSION_MANAGER.setUserSession(httpResponse, user);
        } catch (Exception e) {
            // TODO: 2022/09/10 controllerAdvice에서 잡도록 변경
            httpResponse.sendError();
            return;
        }

        httpResponse.found("/index.html");
    }
}
