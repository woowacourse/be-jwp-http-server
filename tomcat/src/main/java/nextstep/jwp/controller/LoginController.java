package nextstep.jwp.controller;

import java.util.Objects;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    private LoginController() {
        loginService = LoginService.getInstance();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (notAlreadyLoginStatus(request)) {
            response.updateRedirect(request.getHttpVersion(), "/index.html");
            response.addHeader("Set-Cookie", "JSESSIONID=" + request.getSessionId());
            return;
        }
        response.addHeader("Set-Cookie", "JSESSIONID=" + request.getSessionId());
        response.updatePage("/login");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final User loginUser = loginService.login(request);
            log.info("로그인 성공 user = {}", loginUser);
            final Session session = request.getSession();
            session.setAttribute("user", loginUser);
            response.updateRedirect(request.getHttpVersion(), "/index.html");
        } catch (IllegalArgumentException e) {
            response.updateRedirect(request.getHttpVersion(), "/401.html");
        }
    }

    private boolean notAlreadyLoginStatus(final HttpRequest request) {
        final Session session = request.getSession();
        return Objects.nonNull(session.getAttribute("user"));
    }

    public static LoginController getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final LoginController instance = new LoginController();
    }
}
