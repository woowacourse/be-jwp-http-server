package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.domain.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.cookie.HttpCookies;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_KEY = "user";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> loginUserInfo = parseUserInfo(request.getRequestBody());
            User loginUser = login(loginUserInfo);
            setSession(request, response, loginUser);
            response.redirectTo(INDEX_PAGE);
        } catch (IOException | IllegalArgumentException e) {
            log.info("오류 발생: {}", e.getMessage());
            response.redirectTo(UNAUTHORIZED_PAGE);
        }
    }

    private User login(Map<String, String> userInfo) {
        String account = userInfo.get(ACCOUNT_KEY);
        String password = userInfo.get(PASSWORD_KEY);

        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);

        if (loginUser.isEmpty()) {
            throw new IllegalArgumentException(account + "는(은) 등록되지 않은 계정입니다.");
        }

        User user = loginUser.get();

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException(account + "의 비밀번호가 잘못 입력되었습니다.");
        }

        log.info("로그인 성공! user: {}", user);
        return user;
    }

    private void setSession(HttpRequest httpRequest, HttpResponse httpResponse, User user) throws IOException {
        Session session = httpRequest.getSession(sessionManager);
        session.setAttribute(USER_KEY, user);

        httpResponse.setCookie(session.toHeader(session.getId()));
    }

    private Map<String, String> parseUserInfo(Map<String, String> requestBody) {
        Map<String, String> userInfo = new HashMap<>();
        Optional<String> account = Optional.ofNullable(requestBody.get(ACCOUNT_KEY));
        Optional<String> password = Optional.ofNullable(requestBody.get(PASSWORD_KEY));

        if (account.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("필수 입력값이 비어 있습니다.");
        }

        userInfo.put(ACCOUNT_KEY, requestBody.get(ACCOUNT_KEY));
        userInfo.put(PASSWORD_KEY, requestBody.get(PASSWORD_KEY));

        return userInfo;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            HttpCookies cookies = HttpCookies.from(request.getRequestHeader().getCookies());
            String id = cookies.getCookieValue(Session.getSessionKey());

            if (request.existsSession() && sessionManager.findSession(id) != null) {
                response.redirectTo(INDEX_PAGE);
                return;
            }
        } catch (NullPointerException e) {
            log.info("JSESSION 쿠키 값이 없습니다");
            response.redirectTo(LOGIN_PAGE);
        }
        response.redirectTo(LOGIN_PAGE);
    }
}
