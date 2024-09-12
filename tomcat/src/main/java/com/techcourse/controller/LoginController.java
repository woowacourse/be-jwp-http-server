package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String INDEX_PATH = "/index.html";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "=";
    private static final String SESSION_USER_NAME = "user";

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (validateUserInput(httpRequest)) {
            log.error("입력하지 않은 항목이 있습니다.");
            redirectLoginPage(httpRequest, httpResponse);
            return;
        }
        acceptLogin(httpRequest, httpResponse);
    }

    private void redirectLoginPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.found(httpRequest);
        httpResponse.location(LOGIN_PATH);
    }

    private boolean validateUserInput(HttpRequest httpRequest) {
        return !httpRequest.containsBody(ACCOUNT) || !httpRequest.containsBody(PASSWORD);
    }

    private void acceptLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
        String account = httpRequest.getBodyValue(ACCOUNT);
        String password = httpRequest.getBodyValue(PASSWORD);

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 계정입니다"));
        if (user.checkPassword(password)) {
            redirectWithCookie(httpRequest, httpResponse, user);
            return;
        }
        log.error("비밀번호 불일치");
        throw new UnauthorizedException("존재하지 않는 계정입니다");
    }

    private void redirectWithCookie(HttpRequest httpRequest, HttpResponse httpResponse, User user) {
        Session session = httpRequest.getSession();
        session.setAttribute(SESSION_USER_NAME, user);
        log.info(user.toString());
        httpResponse.found(httpRequest);
        httpResponse.setCookie(JSESSIONID + COOKIE_DELIMITER + session.getId());
        httpResponse.location(INDEX_PATH);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        Session session = httpRequest.getSession();
        if (!session.hasAttribute(SESSION_USER_NAME)) {
            httpResponse.ok(httpRequest);
            httpResponse.staticResource(LOGIN_PATH);
            return;
        }
        User user = (User) session.getAttribute(SESSION_USER_NAME);
        log.info(user.toString());
        httpResponse.found(httpRequest);
        httpResponse.setCookie(JSESSIONID + COOKIE_DELIMITER + session.getId());
        httpResponse.location(INDEX_PATH);
    }
}
