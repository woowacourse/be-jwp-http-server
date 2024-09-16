package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.util.NoSuchElementException;

public class LoginController extends AbstractController {

    private static final String USER_SESSION_KEY = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(readResource("login.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpBody body = request.getBody();
        Header header = response.getHeader();
        String account = getAccount(body);
        String password = getPassword(body);

        if (!isLoggedIn(account, password)) {
            response.sendRedirect("401.html");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(USER_SESSION_KEY, getUser(account));
        header.appendJSessionId(session.getId());
        response.sendRedirect("index.html");
    }

    private String getAccount(HttpBody body) {
        return body.get("account")
                .orElseThrow(() -> new IllegalArgumentException("account 값은 필수입니다."));
    }

    private String getPassword(HttpBody body) {
        return body.get("password")
                .orElseThrow(() -> new IllegalArgumentException("password 값은 필수입니다."));
    }

    private boolean isLoggedIn(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    private User getUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이디입니다."));
    }
}