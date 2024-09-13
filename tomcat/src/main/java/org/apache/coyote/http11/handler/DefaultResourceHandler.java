package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Objects;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.exception.CanNotHandleRequest;
import org.apache.coyote.http11.exception.NoSuchUserException;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.HttpRequestParameters;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StaticResource;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class DefaultResourceHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.isStaticResourceRequest()) {
            StaticResource staticResource = new StaticResource(httpRequest.getTarget());
            httpResponse.setResponseOfStaticResource(staticResource);
            return;
        }
        if (httpRequest.getTarget().equals("/")) {
            StaticResource staticResource = new StaticResource("/index.html");
            httpResponse.setResponseOfStaticResource(staticResource);
            return;
        }
        if (httpRequest.getTarget().equals("/login")) {
            loginResponse(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.getTarget().contains("register")) {
            registerResponse(httpRequest, httpResponse);
            return;
        }
        throw new CanNotHandleRequest("처리할 수 없는 요청입니다. : " + httpRequest.getTarget());
    }

    private void loginResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.isPost()) {
            login(httpRequest, httpResponse);
            return;
        }
        if (isLoggedIn(httpRequest)) {
            httpResponse.setMethodFound("/index.html");
            return;
        }
        httpResponse.setMethodFound("/index.html");
    }

    private boolean isLoggedIn(HttpRequest httpRequest) {
        return Objects.nonNull(httpRequest.getSession(false));
    }

    private void login(HttpRequest httpRequest, HttpResponse httpResponse) throws NoSuchUserException {
        HttpRequestParameters requestParams = HttpRequestParameters.parseFrom(httpRequest.getBody());
        String account = requestParams.getParam("account");
        String password = requestParams.getParam("password");
        User user = InMemoryUserRepository.fetchByAccount(account);
        if (user.checkPassword(password)) {
            Session session = httpRequest.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);

            httpResponse.addCookie("JSESSIONID", session.getId());
            httpResponse.setMethodFound("/index.html");
            return;
        }

        httpResponse.setMethodFound("/401.html");
    }

    private void registerResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.isPost()) {
            HttpRequestParameters methodRequest = HttpRequestParameters.parseFrom(httpRequest.getBody());
            User user = register(methodRequest);
            Session session = httpRequest.getSession(true);
            session.setAttribute("user", user);
            SessionManager.getInstance().add(session);

            httpResponse.addCookie("JSESSIONID", session.getId());
            httpResponse.setMethodFound("/index.html");
            return;
        }

        httpResponse.setResponseOfStaticResource(new StaticResource("/register.html"));
    }

    private User register(HttpRequestParameters requestParams) {
        String account = requestParams.getParam("account");
        User user = new User(
                account,
                requestParams.getParam("password"),
                requestParams.getParam("email")
        );
        InMemoryUserRepository.save(user);
        return InMemoryUserRepository.fetchByAccount(account);
    }
}
