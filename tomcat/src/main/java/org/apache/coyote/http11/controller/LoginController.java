package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.Http11Processor.SESSION_MANAGER;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String MAIN_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> userInfos = request.getBody().parseUserInfos();
        final String account = userInfos.get("account");
        final String password = userInfos.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (user.checkPassword(password)) {
            final Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", user);
            SESSION_MANAGER.add(session);

            log.info("로그인 성공! 아이디: {}", account);
            response.setHttpStatus(HttpStatus.FOUND);
            response.sendRedirect(MAIN_PAGE);
            response.addCookie(HttpCookie.ofJSessionId(session.getId()));
            return;
        }
        response.setHttpStatus(HttpStatus.FOUND);
        response.sendRedirect(UNAUTHORIZED_PAGE);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.hasJSessionId()) {
            final Session session = SESSION_MANAGER.findSession(request.getJSessionId());
            validateExistentSession(session);
            response.setHttpStatus(HttpStatus.FOUND);
            response.sendRedirect(MAIN_PAGE);
            return;
        }
        response.setHttpStatus(HttpStatus.OK);
        response.setPath(LOGIN_PAGE);
    }

    private void validateExistentSession(final Session session) {
        if (session == null) {
            throw new IllegalArgumentException("존재하지 않는 세션입니다.");
        }
    }
}
