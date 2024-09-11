package com.techcourse.controller;

import com.techcourse.controller.dto.LoginRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.util.StaticResourceManager;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String STATIC_RESOURCE_PATH = "/login.html";

    @Override
    public String getPath() {
        return "/login";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        // 로그인한 경우 index.html로 리다이렉트
        Optional<HttpSession> session = sessionManager.getSession(request.getSessionId());
        boolean isLogin = session.map(s -> s.getAttribute("user") != null)
                .orElse(false);
        if (isLogin) {
            response.sendRedirect("index.html");
            return;
        }

        ResponseFile file = StaticResourceManager.read(STATIC_RESOURCE_PATH);
        response.setStatus(HttpStatusCode.OK)
                .setBody(file);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        LoginRequest loginRequest = LoginRequest.of(request.getBody());
        log.info("Request Body: {}", loginRequest);

        String userAccount = loginRequest.account();
        String userPassword = loginRequest.password();
        User user = InMemoryUserRepository.findByAccount(userAccount)
                .orElse(null);

        if (user == null || !user.checkPassword(userPassword)) {
            response.sendRedirect("401.html");
            return;
        }

        // 세션이 있으면 세션에 유저 정보를 넣어주고, 없다면 생성해서 넣어줌
        HttpSession httpSession = sessionManager.getSession(request.getSessionId())
                .orElse(new Session(UUID.randomUUID().toString()));
        httpSession.setAttribute("user", user);
        sessionManager.add(httpSession);

        response.sendRedirect("index.html")
                .setCookie("JSESSIONID", httpSession.getId());
    }
}
