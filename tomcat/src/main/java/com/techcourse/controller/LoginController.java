package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.IncorrectPasswordException;
import com.techcourse.exception.UnknownAccountException;
import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import java.util.Map;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends HttpController {
    public LoginController(String path) {
        super(path);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = new ResourceFinder(request.getLocation(), request.getExtension()).getStaticResource(response);
        response.setBody(body);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> payload = request.getPayload();
        String account = payload.get("account");
        String password = payload.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnknownAccountException(account));

        if (!user.checkPassword(password)) {
            throw new IncorrectPasswordException();
        }

        Session loginSession = new Session();
        loginSession.setAttribute("user", user);
        SessionManager.add(loginSession);

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + loginSession.getId() + " ");
    }
}
